package br.com.samuelweb.nfe;

import br.com.samuelweb.nfe.exception.NfeException;
import br.com.samuelweb.nfe.util.CertificadoUtil;
import br.com.samuelweb.nfe.util.ConstantesUtil;
import br.com.samuelweb.nfe.util.WebServiceUtil;
import br.com.samuelweb.nfe.util.XmlUtil;
import br.inf.portalfiscal.nfe.schema_4.consStatServ.TConsStatServ;
import br.inf.portalfiscal.nfe.schema_4.retConsStatServ.TRetConsStatServ;
import br.inf.portalfiscal.nfe_4.wsdl.NFeStatusServico4Stub;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.util.AXIOMUtil;

import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;
import java.rmi.RemoteException;


/**
 * Classe responsavel por fazer a Verificacao do Status Do Webservice
 * 
 * @author Samuel Oliveira
 *
 */
public class Status {

	/**
	 * Metodo para Consulta de Status de Serviço
	 *
	 * @param tipo ConstantesUtil.NFE e ConstantesUtil.NFCE
	 * @return
	 * @throws NfeException
	 */
	static TRetConsStatServ statusServico(String tipo) throws NfeException {

		try {
			ConfiguracoesIniciaisNfe configuracoesNfe = CertificadoUtil.iniciaConfiguracoes();

			TConsStatServ consStatServ = new TConsStatServ();
			consStatServ.setTpAmb(configuracoesNfe.getAmbiente());
			consStatServ.setCUF(configuracoesNfe.getEstado().getCodigoIbge());
			consStatServ.setVersao(configuracoesNfe.getVersaoNfe());
			consStatServ.setXServ("STATUS");
			String xml = XmlUtil.objectToXml(consStatServ);
	
			System.out.println("Xml Status: "+xml);
			OMElement ome = AXIOMUtil.stringToOM(xml);

			NFeStatusServico4Stub.NfeDadosMsg dadosMsg = new NFeStatusServico4Stub.NfeDadosMsg();
			dadosMsg.setExtraElement(ome);

			NFeStatusServico4Stub stub = new NFeStatusServico4Stub(tipo.equals(ConstantesUtil.NFCE) ? WebServiceUtil.getUrl(ConstantesUtil.NFCE, ConstantesUtil.SERVICOS.STATUS_SERVICO) : WebServiceUtil.getUrl(ConstantesUtil.NFE, ConstantesUtil.SERVICOS.STATUS_SERVICO));
			NFeStatusServico4Stub.NfeResultMsg result = stub.nfeStatusServicoNF(dadosMsg);

			return XmlUtil.xmlToObject(result.getExtraElement().toString(), TRetConsStatServ.class);

		} catch (RemoteException | XMLStreamException | JAXBException e) {
			throw new NfeException(e.getMessage());
		}
		
	}
	
}