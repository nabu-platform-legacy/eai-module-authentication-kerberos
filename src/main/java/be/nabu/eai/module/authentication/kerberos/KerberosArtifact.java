/*
* Copyright (C) 2016 Alexander Verbruggen
*
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU Lesser General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* GNU Lesser General Public License for more details.
*
* You should have received a copy of the GNU Lesser General Public License
* along with this program. If not, see <https://www.gnu.org/licenses/>.
*/

package be.nabu.eai.module.authentication.kerberos;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import be.nabu.eai.module.web.application.WebApplication;
import be.nabu.eai.module.web.application.WebFragment;
import be.nabu.eai.module.web.application.WebFragmentPriority;
import be.nabu.eai.repository.api.Repository;
import be.nabu.eai.repository.artifacts.jaxb.JAXBArtifact;
import be.nabu.libs.authentication.api.Permission;
import be.nabu.libs.authentication.jaas.JAASConfiguration;
import be.nabu.libs.events.api.EventHandler;
import be.nabu.libs.events.api.EventSubscription;
import be.nabu.libs.http.api.HTTPRequest;
import be.nabu.libs.http.api.HTTPResponse;
import be.nabu.libs.http.server.FixedRealmHandler;
import be.nabu.libs.http.server.HTTPServerUtils;
import be.nabu.libs.http.server.kerberos.KerberosAuthenticationListener;
import be.nabu.libs.resources.api.ResourceContainer;
import be.nabu.utils.mime.api.Header;
import be.nabu.utils.mime.impl.MimeHeader;
import be.nabu.utils.mime.impl.MimeUtils;

public class KerberosArtifact extends JAXBArtifact<KerberosConfiguration> implements WebFragment {

	private Map<String, List<EventSubscription<?, ?>>> subscriptions = new HashMap<String, List<EventSubscription<?, ?>>>();
	
	public KerberosArtifact(String id, ResourceContainer<?> directory, Repository repository) {
		super(id, directory, repository, "kerberos.xml", KerberosConfiguration.class);
	}

	@Override
	public void start(WebApplication artifact, String path) throws IOException {
		String key = getKey(artifact, path);
		if (subscriptions.containsKey(key)) {
			stop(artifact, path);
		}
		// the options for the kerberos configuration
		Map<String, String> options = new HashMap<String, String>();
		options.put("storeKey", "true");
		options.put("useKeyTab", "false");
		options.put("principal", getConfiguration().getPrincipal());
        options.put("isInitiator", "false");
        
        // make sure it is registered
        JAASConfiguration.register();
		JAASConfiguration.getInstance().configure(getId(), JAASConfiguration.newKerberosEntry(options));
		String kerberosRealm = getConfiguration().getKerberosRealm();
		if (kerberosRealm == null && getConfiguration().getUseWebRealm() != null && getConfiguration().getUseWebRealm()) {
			kerberosRealm = artifact.getRealm();
		}
		KerberosAuthenticationListener kerberosAuthenticationListener = new KerberosAuthenticationListener(getId(), kerberosRealm, getConfiguration().getPrincipal(), getConfiguration().getPassword(), new FixedRealmHandler(artifact.getRealm()));
		kerberosAuthenticationListener.setRequired(false);
		synchronized(subscriptions) {
			EventSubscription<HTTPRequest, HTTPResponse> subscription = artifact.getDispatcher().subscribe(HTTPRequest.class, kerberosAuthenticationListener);
			subscription.filter(HTTPServerUtils.limitToPath(getFullPath(artifact, path)));
			if (!subscriptions.containsKey(key)) {
				subscriptions.put(key, new ArrayList<EventSubscription<?, ?>>());
			}
			subscriptions.get(key).add(subscription);
			
			if (getConfiguration().getPrompt() != null && getConfiguration().getPrompt()) {
				EventSubscription<HTTPResponse, HTTPResponse> promptSubscription = artifact.getDispatcher().subscribe(HTTPResponse.class, new EventHandler<HTTPResponse, HTTPResponse>() {
					@Override
					public HTTPResponse handle(HTTPResponse event) {
						if (event.getContent() != null && event.getCode() == 401) {
							Header[] headers = MimeUtils.getHeaders("WWW-Authenticate", event.getContent().getHeaders());
							boolean addHeader = headers == null || headers.length == 0;
							if (addHeader) {
								for (Header header : headers) {
									if (header.getValue().startsWith("Negotiate")) {
										addHeader = false;
										break;
									}
								}
							}
							if (addHeader) {
								event.getContent().setHeader(new MimeHeader("WWW-Authenticate", "Negotiate"));
							}
						}
						return null;
					}
				});
				subscriptions.get(key).add(promptSubscription);
			}
		}
	}

	@Override
	public void stop(WebApplication artifact, String path) {
		String key = getKey(artifact, path);
		if (subscriptions.containsKey(key)) {
			synchronized(subscriptions) {
				if (subscriptions.containsKey(key)) {
					for (EventSubscription<?, ?> subscription : subscriptions.get(key)) {
						subscription.unsubscribe();
					}
					subscriptions.remove(key);
				}
			}
		}
		JAASConfiguration.getInstance().remove(getId());
	}

	@Override
	public List<Permission> getPermissions(WebApplication artifact, String path) {
		return null;
	}

	@Override
	public boolean isStarted(WebApplication artifact, String path) {
		return subscriptions.containsKey(getKey(artifact, path));
	}

	private String getKey(WebApplication artifact, String path) {
		return artifact.getId() + ":" + path;
	}
	
	public WebFragmentPriority getPriority() {
		return WebFragmentPriority.HIGH;
	}
	
	String getFullPath(WebApplication artifact, String path) throws IOException {
		String wsdlPath = artifact.getServerPath();
		if (path != null && !path.isEmpty() && !path.equals("/")) {
			if (!wsdlPath.endsWith("/")) {
				wsdlPath += "/";
			}
			wsdlPath += path.replaceFirst("^[/]+", "");
		}
		if (getConfiguration().getPath() != null) {
			wsdlPath += "/" + getConfiguration().getPath().replaceFirst("^[/]+", "");
		}
		return wsdlPath.replace("//", "/");
	}
}
