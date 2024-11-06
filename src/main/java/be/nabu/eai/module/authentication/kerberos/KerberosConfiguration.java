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

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import be.nabu.eai.api.EnvironmentSpecific;
import be.nabu.eai.api.InterfaceFilter;
import be.nabu.eai.repository.jaxb.ArtifactXMLAdapter;
import be.nabu.libs.services.api.DefinedService;
import be.nabu.utils.security.EncryptionXmlAdapter;

@XmlRootElement(name = "kerberos")
public class KerberosConfiguration {
	
	private String principal, password, kerberosRealm;
	private String path;
	private Boolean useWebRealm;
	private DefinedService authenticatorService;
	private Boolean prompt;

	@EnvironmentSpecific
	public String getPrincipal() {
		return principal;
	}
	public void setPrincipal(String principal) {
		this.principal = principal;
	}

	@EnvironmentSpecific
	@XmlJavaTypeAdapter(value=EncryptionXmlAdapter.class)
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	
	@EnvironmentSpecific
	public String getKerberosRealm() {
		return kerberosRealm;
	}
	public void setKerberosRealm(String kerberosRealm) {
		this.kerberosRealm = kerberosRealm;
	}
	
	public Boolean getUseWebRealm() {
		return useWebRealm;
	}
	public void setUseWebRealm(Boolean useWebRealm) {
		this.useWebRealm = useWebRealm;
	}
	
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}

	@InterfaceFilter(implement = "be.nabu.libs.http.server.kerberos.KerberosAuthenticator.authenticate")
	@XmlJavaTypeAdapter(value = ArtifactXMLAdapter.class)
	public DefinedService getAuthenticatorService() {
		return authenticatorService;
	}
	public void setAuthenticatorService(DefinedService authenticatorService) {
		this.authenticatorService = authenticatorService;
	}
	
	public Boolean getPrompt() {
		return prompt;
	}
	public void setPrompt(Boolean prompt) {
		this.prompt = prompt;
	}

}
