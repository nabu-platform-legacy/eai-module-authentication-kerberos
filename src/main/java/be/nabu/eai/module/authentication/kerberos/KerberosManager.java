package be.nabu.eai.module.authentication.kerberos;

import be.nabu.eai.repository.api.Repository;
import be.nabu.eai.repository.managers.base.JAXBArtifactManager;
import be.nabu.libs.resources.api.ResourceContainer;

public class KerberosManager extends JAXBArtifactManager<KerberosConfiguration, KerberosArtifact> {

	public KerberosManager() {
		super(KerberosArtifact.class);
	}

	@Override
	protected KerberosArtifact newInstance(String id, ResourceContainer<?> container, Repository repository) {
		return new KerberosArtifact(id, container, repository);
	}

}
