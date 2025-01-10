package pe.gob.sunass.vma.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import pe.gob.sunass.vma.model.UsuarioLdap;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class LdapUtil {

	@Value("${ldap.prefix}")
    private String ldapPrefix;
	
	@Value("${ldap.cn}")
    private String ldapCn;
	
	@Value("${ldap.cnSearch}")
    private String ldapCnSearch;
	
	@Value("${ldap.timeout}")
    private String ldapTimeout;
	
	@Value("${ldap.url}")
    private String ldapUrl;
	
	@Value("${ldap.user}")
    private String ldapUser;
	
	@Value("${ldap.password}")
    private String ldapPassword;
	
	@Value("${ldap.group}")
    private String ldapGroup;
	
	private static final Logger log = LoggerFactory.getLogger(LdapUtil.class);
    
	public String ldapProviderUrl() {
		return this.ldapUrl+"/"+this.ldapCnSearch;
	}
	
    public DirContext conectarLDAP() throws Exception {
        
        String ldapPrefix = this.ldapPrefix;
        String ldapTimeout = this.ldapTimeout;
        String ldapUrl = this.ldapUrl;
        String ldapUser = this.ldapUser;
        String ldapPassword = this.ldapPassword;
        
        DirContext dirContext = null;
        Hashtable env = new Hashtable();
        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        env.put(Context.PROVIDER_URL, ldapUrl);
        env.put(Context.SECURITY_AUTHENTICATION, "simple");
        env.put(Context.SECURITY_PRINCIPAL, ldapUser + ldapPrefix);
        env.put(Context.SECURITY_CREDENTIALS, ldapPassword);
        env.put("com.sun.jndi.ldap.connect.timeout", ldapTimeout);
        
        dirContext = new InitialDirContext(env);
        
        return dirContext;
    }
    
    public NamingEnumeration<?> obtenerGruposLDAP(DirContext dirContext) throws Exception {
        
        String ldapGroup = this.ldapGroup;
        String ldapCN = this.ldapCnSearch;
        
        NamingEnumeration<?> usersLDAP = null;
        
        SearchControls constraints = new SearchControls();

        constraints.setReturningAttributes(new String[]{"cn", "member"});
        constraints.setSearchScope(SearchControls.SUBTREE_SCOPE);
        NamingEnumeration<SearchResult> groupAnswer = dirContext.search(ldapCN, "(&(objectCategory=group)(cn=" + ldapGroup + "))", constraints);

        if (groupAnswer.hasMore()) {
            Attributes attrs = ((SearchResult) groupAnswer.next()).getAttributes();
            log.debug("Group: " + attrs.get("cn").get());
            if (attrs.get("member") != null) {
                usersLDAP = attrs.get("member").getAll();
            }
        } else {
            log.debug("Not exists group");
        }
              
        return usersLDAP;
    }

    public Attributes obtenerUsuarioLDAP(DirContext dirContext, String userCN) throws Exception {
    	
    	String unidadOrganica="";
    	 Attributes attrs = null;
    	 SearchControls constraints = new SearchControls();
        constraints.setReturningAttributes(new String[] {"sn", "givenName", "SAMAccountName"});
        constraints.setSearchScope(SearchControls.SUBTREE_SCOPE);
        String filter = "(&(objectCategory=person)(objectClass=user))";
        NamingEnumeration<SearchResult> userLDAP = dirContext.search(userCN, filter, constraints);
        if (userLDAP.hasMore()) {
            attrs = ((SearchResult) userLDAP.next()).getAttributes();
           // unidadOrganica=  (String) (attrs.get("physicalDeliveryOfficeName")==null?"":attrs.get("physicalDeliveryOfficeName").get()) ;
            
        }
        return attrs;
    }
    
    public List<UsuarioLdap> buscarUsuariosLDAP() throws Exception{
        
        List<UsuarioLdap> listaUsuario=new ArrayList<>();
        DirContext dirContext = this.conectarLDAP();
        NamingEnumeration<?> gruposLDAP = this.obtenerGruposLDAP(dirContext);

        if (gruposLDAP != null) {
            while (gruposLDAP.hasMore()) {
                String userCN = gruposLDAP.next().toString();
                Attributes attrs = this.obtenerUsuarioLDAP(dirContext, userCN);
                if (attrs != null && attrs.size() == 3) {
                    UsuarioLdap userLdap=new UsuarioLdap();
                    userLdap.setCuenta( (String) attrs.get("SAMAccountName").get() );
                    userLdap.setNombres( ((String) attrs.get("givenName").get()).toUpperCase().trim() );
                    userLdap.setUsuario( ((String) attrs.get("sn").get()).toUpperCase().trim() );
                   // userLdap.setUnidadOrganica(((String) attrs.get("physicalDeliveryOfficeName").get()).toUpperCase().trim());
                    listaUsuario.add(userLdap);
                }
            }
        }
        return listaUsuario;
    }
    
    public DirContext validar(String usuario,String clave) throws Exception {
        
        String ldapPrefix = this.ldapPrefix;
        String ldapTimeout = this.ldapTimeout;
        String ldapUrl = this.ldapUrl;
        String ldapUser = usuario;
        String ldapPassword = clave;
        
        DirContext dirContext = null;
        Hashtable env = new Hashtable();
        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        env.put(Context.PROVIDER_URL, ldapUrl);
        env.put(Context.SECURITY_AUTHENTICATION, "simple");
        env.put(Context.SECURITY_PRINCIPAL, ldapUser + ldapPrefix);
        env.put(Context.SECURITY_CREDENTIALS, ldapPassword);
        env.put("com.sun.jndi.ldap.connect.timeout", ldapTimeout);
        
        dirContext = new InitialDirContext(env);
        
        return dirContext;
    }
    
    public DirContext validarWithOutSimpleAuthenticacion(String usuario, String clave) throws NamingException {
        
        String ldapPrefix = this.ldapPrefix;
        String ldapTimeout = this.ldapTimeout;
        String ldapUrl = this.ldapUrl;
        String ldapCN = this.ldapCnSearch;

        String ldapPassword = clave;
        
        String ldapProviderURl = this.ldapProviderUrl();
        
        String userName = usuario;
       // String unidadOrganica= "";
        
        if (!userName.contains("@")) {
			userName = userName + ldapPrefix;
		}
        
        DirContext dirContext = null;
        
        Properties properties = new Properties();
		properties.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
		properties.put(Context.PROVIDER_URL, ldapProviderURl);
		// properties.put(Context.SECURITY_AUTHENTICATION, "simple");
		properties.put(Context.SECURITY_PRINCIPAL, userName);
		properties.put(Context.SECURITY_CREDENTIALS, ldapPassword);
		properties.put("com.sun.jndi.ldap.connect.timeout", "3000");
        
        /*
        try {
			dirContext = new InitialDirContext(env);
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
        
        dirContext = new InitialDirContext(properties);
        
        return dirContext;
    }
    
    public Attributes obtenerInfoUserLdap(String userName, String password) throws Exception {
    	
    	String ldapPrefix = this.ldapPrefix;
    	
    	DirContext context = null;
    	
    	Attributes attributes = null;
    	
    	NamingEnumeration<SearchResult> results = null;
    	
    	if (!userName.contains("@")) {
			userName = userName + ldapPrefix;
		}
    	
    	context = this.validarWithOutSimpleAuthenticacion(userName, password);
		
    	if(context != null)
    	{
			String userName2 = userName.replaceAll("(.+)@(.+)", "$1");
						
			SearchControls controls = new SearchControls();
			controls.setSearchScope(SearchControls.SUBTREE_SCOPE);
			// controls.setReturningAttributes(new String[] {"givenName","cn","sn"});
			results = context.search("",
					String.format("(&(objectClass=user)(objectCategory=Person)(sAMAccountName=%s))", userName2),
					controls);
			
			SearchResult result = results.next();
			
			attributes = result.getAttributes();
    	}
    	
		return attributes;
    }
    
    /* LDAP X GRUPO */
    public NamingEnumeration<?> obtenerGruposLDAP(DirContext dirContext,String grupo) throws Exception {

        String ldapCN = this.ldapCnSearch;

        NamingEnumeration<?> usersLDAP = null;

        SearchControls constraints = new SearchControls();

        constraints.setReturningAttributes(new String[]{"cn", "member"});
        constraints.setSearchScope(SearchControls.SUBTREE_SCOPE);
        NamingEnumeration<SearchResult> groupAnswer = dirContext.search(ldapCN, "(&(objectCategory=group)(cn=" + grupo + "))", constraints);

        if (groupAnswer.hasMore()) {
            Attributes attrs = ((SearchResult) groupAnswer.next()).getAttributes();
            log.debug("Group: " + attrs.get("cn").get());
            if (attrs.get("member") != null) {
                usersLDAP = attrs.get("member").getAll();
            }
        } else {
            log.debug("Not exists group");
        }

        return usersLDAP;
    }
    
    public List<UsuarioLdap> buscarUsuariosLdapPorGrupo(String grupo) throws Exception {


        List<UsuarioLdap> listaUsuario = new ArrayList<>();
        DirContext dirContext = this.conectarLDAP();
        NamingEnumeration<?> gruposLDAP = this.obtenerGruposLDAP(dirContext,grupo);

        if (gruposLDAP != null) {
            while (gruposLDAP.hasMore()) {
                String userCN = gruposLDAP.next().toString();
                Attributes attrs = this.obtenerUsuarioLDAP(dirContext, userCN);
                if (attrs != null && attrs.size() == 3) {
                    UsuarioLdap userLdap = new UsuarioLdap();
                    userLdap.setCuenta((String) attrs.get("SAMAccountName").get());
                    userLdap.setNombres(((String) attrs.get("givenName").get()).toUpperCase().trim());
                    userLdap.setUsuario(((String) attrs.get("sn").get()).toUpperCase().trim());

                    listaUsuario.add(userLdap);
                }
            }
        }
        return listaUsuario;
    }
    
    public List<UsuarioLdap> listarTodosUsuariosLDAP() throws Exception{
        
        List<UsuarioLdap> listaUsuario = new ArrayList<>();
        
        DirContext ldapContext = conectarLDAP();
        
        SearchControls searchCtls = new SearchControls();
        String returnedAtts[]={"sn","givenName", "samAccountName"};
        searchCtls.setReturningAttributes(returnedAtts);
        searchCtls.setSearchScope(SearchControls.SUBTREE_SCOPE);
        String searchFilter = "(&(objectClass=user))";
        
        //String searchBase = "OU=Usuarios,DC=sunass,DC=gob,DC=pe";
        String searchBaseLima = "OU=Lima,OU=Usuarios,DC=sunass,DC=gob,DC=pe";
        String searchBaseProvincia = "OU=Provincias,OU=Usuarios,DC=sunass,DC=gob,DC=pe";

        
        NamingEnumeration<SearchResult> answerLima = ldapContext.search(searchBaseLima, searchFilter, searchCtls);
        NamingEnumeration<SearchResult> answerProvincia = ldapContext.search(searchBaseProvincia, searchFilter, searchCtls);

        while (answerLima.hasMoreElements()){
            SearchResult sr = (SearchResult)answerLima.next();
            Attributes attrs = sr.getAttributes();
            UsuarioLdap userLdap = new UsuarioLdap();
            userLdap.setCuenta((String) (attrs.get("SAMAccountName")==null?"":attrs.get("SAMAccountName").get()) );
            userLdap.setNombres(((String) (attrs.get("givenName")==null?"":attrs.get("givenName").get()) ).toUpperCase().trim());
            userLdap.setApellidos(((String) (attrs.get("sn")==null?"":attrs.get("sn").get()) ).toUpperCase().trim());
            userLdap.setUsuario(((String) (attrs.get("sn")==null?"":attrs.get("sn").get()) ).toUpperCase().trim());
            listaUsuario.add(userLdap);
        }

        while (answerProvincia.hasMoreElements()){
            SearchResult sr = (SearchResult)answerProvincia.next();
            Attributes attrs = sr.getAttributes();
            UsuarioLdap userLdap = new UsuarioLdap();
            userLdap.setCuenta((String) (attrs.get("SAMAccountName")==null?"":attrs.get("SAMAccountName").get()) );
            userLdap.setNombres(((String) (attrs.get("givenName")==null?"":attrs.get("givenName").get()) ).toUpperCase().trim());
            userLdap.setUsuario(((String) (attrs.get("sn")==null?"":attrs.get("sn").get()) ).toUpperCase().trim());
            listaUsuario.add(userLdap);
        }
        
        ldapContext.close();
        return listaUsuario;
    }
}
