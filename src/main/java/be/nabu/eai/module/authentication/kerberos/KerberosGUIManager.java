package be.nabu.eai.module.authentication.kerberos;

import java.io.IOException;
import java.util.List;

import be.nabu.eai.developer.MainController;
import be.nabu.eai.developer.managers.base.BaseJAXBGUIManager;
import be.nabu.eai.repository.resources.RepositoryEntry;
import be.nabu.libs.property.api.Property;
import be.nabu.libs.property.api.Value;

public class KerberosGUIManager extends BaseJAXBGUIManager<KerberosConfiguration, KerberosArtifact> {

	public KerberosGUIManager() {
		super("Kerberos Server Authentication", KerberosArtifact.class, new KerberosManager(), KerberosConfiguration.class);
	}

	@Override
	protected List<Property<?>> getCreateProperties() {
		return null;
	}

	@Override
	protected KerberosArtifact newInstance(MainController controller, RepositoryEntry entry, Value<?>... values) throws IOException {
		return new KerberosArtifact(entry.getId(), entry.getContainer(), entry.getRepository());
	}

	@Override
	public String getCategory() {
		return "Authentication";
	}
}
