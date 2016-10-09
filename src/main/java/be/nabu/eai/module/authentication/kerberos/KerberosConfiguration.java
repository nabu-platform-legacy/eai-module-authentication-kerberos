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
