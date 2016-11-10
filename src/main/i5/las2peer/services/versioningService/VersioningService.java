package i5.las2peer.services.versioningService;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.Base64;
import java.util.Collections;
import java.util.Date;
import java.util.logging.Level;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.StoredConfig;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.RefSpec;
import org.eclipse.jgit.transport.RemoteConfig;
import org.eclipse.jgit.transport.URIish;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.eclipse.jgit.treewalk.TreeWalk;

import i5.las2peer.api.Service;
import i5.las2peer.logging.L2pLogger;
import i5.las2peer.logging.NodeObserver.Event;
import i5.las2peer.p2p.ArtifactNotFoundException;
import i5.las2peer.restMapper.annotations.ContentParam;
import i5.las2peer.restMapper.HttpResponse;
import i5.las2peer.restMapper.MediaType;
import i5.las2peer.restMapper.RESTMapper;
import i5.las2peer.restMapper.annotations.Version;
import i5.las2peer.restMapper.tools.ValidationResult;
import i5.las2peer.restMapper.tools.XMLCheck;
import i5.las2peer.services.versioningService.exception.GitHubException;
import io.swagger.annotations.*;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;  
import com.google.gson.JsonObject;




// TODO Describe your own service
/**
 * LAS2peer Service
 * 
 * This is a template for a very basic LAS2peer service that uses the LAS2peer
 * Web-Connector for RESTful access to it.
 * 
 * Note: If you plan on using Swagger you should adapt the information below in
 * the ApiInfo annotation to suit your project. If you do not intend to provide
 * a Swagger documentation of your service API, the entire ApiInfo annotation
 * should be removed.
 * 
 */
// TODO Adjust the following configuration
@Path("/template")
@Version("0.1") // this annotation is used by the XML mapper
@Api
@SwaggerDefinition(info = @Info(title = "Co-Design Project Generation Service", version = "0.1", description = "For co-design project", termsOfService = "http://your-terms-of-service-url.com", contact = @Contact(name = "John Doe", url = "provider.com", email = "john.doe@provider.com"), license = @License(name = "your software license name", url = "http://your-software-license-url.com")))

// TODO Your own Serviceclass
public class VersioningService extends Service {

	
	private String gitHubClientID;
	private String gitHubClientSecret;
	
	private String authorizationCode;
	// instantiate the logger class
	private final L2pLogger logger = L2pLogger.getInstance(VersioningService.class.getName());

	public VersioningService() {
		// read and set properties values
		// IF THE SERVICE CLASS NAME IS CHANGED, THE PROPERTIES FILE NAME NEED
		// TO BE CHANGED TOO!
		setFieldValues();
	}

	// //////////////////////////////////////////////////////////////////////////////////////
	// Service methods.
	// //////////////////////////////////////////////////////////////////////////////////////

	// TODO OWN METHODS

	/**
	 * Test of a basic get function.
	 * 
	 * @return HttpResponse with the returnString
	 */
	@GET
	@Path("/testsimpleget")
	@Produces(MediaType.TEXT_PLAIN)
	@ApiOperation(value = "REPLACE THIS WITH AN APPROPRIATE FUNCTION NAME", notes = "REPLACE THIS WITH YOUR NOTES TO THE FUNCTION")
	@ApiResponses(value = { @ApiResponse(code = HttpURLConnection.HTTP_OK, message = "Test success"),
			@ApiResponse(code = HttpURLConnection.HTTP_UNAUTHORIZED, message = "Unauthorized") })
	public HttpResponse getTemplate() {
		String returnString = "get testSimpleGet return, success!";
		// HttpResponse response = new HttpResponse();
		return new HttpResponse(returnString, HttpURLConnection.HTTP_OK);
	}

	
	/**
	 * Get GitHub return code, in order to exchange access token in the future
	 * 
	 * @return HttpResponse with the returnString
	 */
	@GET
	@Path("/callbacktest")
	@Produces(MediaType.TEXT_PLAIN)
	@ApiResponses(value = {
			@ApiResponse(code = HttpURLConnection.HTTP_OK, message = "REPLACE THIS WITH YOUR OK MESSAGE"),
			@ApiResponse(code = HttpURLConnection.HTTP_UNAUTHORIZED, message = "Unauthorized")
	})
	@ApiOperation(value = "REPLACE THIS WITH AN APPROPRIATE FUNCTION NAME",
			notes = "Example method that returns a phrase containing the received input.")
	public HttpResponse tryCallback(@QueryParam("code") String code) {
		String returnString = "";
		System.out.println("code parameter = "+code);
		returnString += code;
		return new HttpResponse(returnString, HttpURLConnection.HTTP_OK);
	}
	
	
	/**
	 * Get GitHub repos
	 * 
	 * @return HttpResponse with the returnString
	 */
	@GET
	@Path("/projects")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiResponses(value = {
			@ApiResponse(code = HttpURLConnection.HTTP_OK, message = "REPLACE THIS WITH YOUR OK MESSAGE"),
			@ApiResponse(code = HttpURLConnection.HTTP_UNAUTHORIZED, message = "Unauthorized")
	})
	@ApiOperation(value = "REPLACE THIS WITH AN APPROPRIATE FUNCTION NAME",
			notes = "Example method that returns a phrase containing the received input.")
	public HttpResponse showAllRepos() {
		try{
			URL url = new URL("https://api.github.com/orgs/Co-Design-Platform/repos");
			System.out.println(url);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			//connection.setRequestProperty("User-Agent", appName);
			connection.setRequestMethod("GET");
			connection.setUseCaches(false);
			connection.setDoInput(true);
			connection.setDoOutput(true);
			connection.setConnectTimeout(7000);
			
			String outputString = "";
			BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String line;
			reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			while ((line = reader.readLine()) != null) {
				outputString = outputString + line;
			}
			reader.close();
			//System.out.println(outputString);
			HttpResponse OrgRes = new HttpResponse(outputString,HttpURLConnection.HTTP_OK);
			return OrgRes;
		}catch (Exception e) {
			logger.log(Level.SEVERE, "showAllRepos problem:", e);
			logger.printStackTrace(e);
			return new HttpResponse(e.getMessage(), 500);
		}
	}
	
	/**
	 * Get GitHub authenticated user 
	 * see https://developer.github.com/v3/users/#get-the-authenticated-user
	 * 
	 * @return HttpResponse with the returnString
	 */
	@GET
	@Path("/getCurrentGitHubUser")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiResponses(value = {
			@ApiResponse(code = HttpURLConnection.HTTP_OK, message = "REPLACE THIS WITH YOUR OK MESSAGE"),
			@ApiResponse(code = HttpURLConnection.HTTP_UNAUTHORIZED, message = "Unauthorized")
	})
	@ApiOperation(value = "REPLACE THIS WITH AN APPROPRIATE FUNCTION NAME",
			notes = "Example method that returns a phrase containing the received input.")
	public HttpResponse getCurrentGitHubUser(@HeaderParam(value = HttpHeaders.AUTHORIZATION) String accessToken) {
		try{
			URL url = new URL("https://api.github.com/user");
			System.out.println(url);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("GET");
			//connection.setRequestProperty ("Authorization", "token "+accessToken);
			connection.setRequestProperty ("Authorization", accessToken);
			connection.setUseCaches(false);
			connection.setDoInput(true);
			connection.setDoOutput(true);
			connection.setConnectTimeout(7000);
			
			String outputString = "";
			BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String line;
			reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			while ((line = reader.readLine()) != null) {
				outputString = outputString + line;
			}
			reader.close();
			//System.out.println(outputString);
			HttpResponse user = new HttpResponse(outputString,HttpURLConnection.HTTP_OK);
			return user;
		}catch (Exception e) {
			logger.log(Level.SEVERE, "getCurrentGitHubUser error",e);
			logger.printStackTrace(e);
			return new HttpResponse(e.getMessage(), 500);
		}
	}
	
	
	/**
	 * Get use code to get GitHub access_token
	 * 
	 * @return HttpResponse with the returnString
	 */
	@GET
	@Path("/githubaccess")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiResponses(value = {
			@ApiResponse(code = HttpURLConnection.HTTP_OK, message = "REPLACE THIS WITH YOUR OK MESSAGE"),
			@ApiResponse(code = HttpURLConnection.HTTP_UNAUTHORIZED, message = "Unauthorized")
	})
	public HttpResponse getGitHubToken(@QueryParam("code") String code) {
		try{		
			String returnString = null;

			if (code == null) {
				/*URI url = new URI("https://github.com/login/oauth/authorize?client_id="+ this.gitHubClientID);				
				return(Response.temporaryRedirect(url).build());*/ // how to redirect directly? Response type, not HttpResponse
				returnString = "Sorry. Please Sign In GitHub.";
				return new HttpResponse (returnString, HttpURLConnection.HTTP_OK);
			}
			else{
				URL url = new URL("https://github.com/login/oauth/access_token?client_id="
							+ this.gitHubClientID 
							//+ "&redirect_uri=" + redirectURI
							+ "&client_secret=" + this.gitHubClientSecret + "&code=" +code);
				
				HttpURLConnection connection = (HttpURLConnection) url.openConnection();			
				connection.setRequestMethod("POST");
				connection.setConnectTimeout(20000);
				
				String outputString = "";
				BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
				String line;
				while ((line = reader.readLine()) != null) {
					outputString = outputString + line;
				}
				logger.log(Level.INFO, "request url:"+ url.toString());
				logger.log(Level.INFO,outputString);
				String accessToken = null;
				
				/*If the verification code you pass is incorrect, expired, 
				 * or doesn't match what you received in the first request for authorization 
				 * you will receive this error.{
				 * "error": "bad_verification_code",
				 * "error_description": "The code passed is incorrect or expired.",
				 * "error_uri": "https://developer.github.com/v3/oauth/#bad-verification-code"
				 * }
				 * To solve this error, start the OAuth process over from the beginning and get a new code.
				*/
				if(outputString.indexOf("bad_verification_code") != -1){
					returnString = "request:"+url.toString()+": Authorization fail(incorrect/expire/not match), please authorize again.";
				}
				else if (outputString.indexOf("access_token") != -1) {
					accessToken = outputString.substring(13,outputString.indexOf("&"));
					returnString = accessToken;
				}
				logger.log(Level.INFO,"accessToken: "+accessToken);
				Gson gson = new Gson();
		        return new HttpResponse(gson.toJson(returnString), HttpURLConnection.HTTP_OK);
			}
		 }catch (Exception e) {
				logger.log(Level.SEVERE, "getGitHubToken problem:", e);
				logger.printStackTrace(e);
				return new HttpResponse(e.getMessage(), 500);
		}
	}

	/**
     * This method allows to create a new project(a GitHub repo).
     *
     * @param project project as a JSON object
     * @return Response with the created project as a JSON object.
     */
	@POST
	@Path("/createproject")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "This method allows to create a new project")
    @ApiResponses(value = {
            @ApiResponse(code = HttpURLConnection.HTTP_CREATED, message = "Returns the created project"),
            @ApiResponse(code = HttpURLConnection.HTTP_UNAUTHORIZED, message = "Unauthorized"),
            @ApiResponse(code = HttpURLConnection.HTTP_INTERNAL_ERROR, message = "Internal server problems")
    })
	public HttpResponse createProject(@HeaderParam(
			value = HttpHeaders.AUTHORIZATION) String accessToken, @ContentParam String project) {
		String returnString = "";
		try {
			
			logger.log(Level.INFO, "Create a new repo");
			logger.log(Level.INFO,"access token:"+ accessToken);
			logger.log(Level.INFO, project);
			
//			JsonObject jobj = new Gson().fromJson(project, JsonObject.class);
//			logger.log(Level.INFO, jobj.get("name").toString());
//			logger.log(Level.INFO, jobj.get("description").toString());
//			logger.log(Level.INFO, jobj.toString());

			URL url = new URL("https://api.github.com/orgs/Co-Design-Platform/repos");

			HttpURLConnection connection = (HttpURLConnection) url.openConnection();	
			//connection.setRequestProperty ("Authorization", "token "+accessToken);
			connection.setRequestProperty ("Authorization", accessToken);

			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type", "application/json");
			connection.setConnectTimeout(20000);
			connection.setDoOutput(true);
			
			logger.log(Level.INFO,"request url:"+connection.getURL().toString());		
					
			OutputStream os = connection.getOutputStream();
			os.write(project.getBytes("UTF-8"));
			os.flush();

			if (connection.getResponseCode() != HttpURLConnection.HTTP_CREATED) {
//				throw new RuntimeException("code : "
//						+ connection.getResponseCode()+",msg: "+connection.getResponseMessage());	
				BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
				String line;
				String errorString="";
				while ((line = reader.readLine()) != null) {
					errorString = errorString + line;
				}
				throw new GitHubException(connection.getResponseCode(), connection.getResponseMessage(), errorString);
			}

			
			
//			if (200 <= conn.getResponseCode() && conn.getResponseCode() <= 299) {
//			    br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
//			} else {
//			    br = new BufferedReader(new InputStreamReader((conn.getErrorStream())));
//			}
			
			String outputString = "";
			BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String line;
			while ((line = reader.readLine()) != null) {
				outputString = outputString + line;
			}

			//the response header fields
			logger.log(Level.INFO,"getHeaderFields():"+connection.getHeaderFields().toString());

			//when you call server.getResponseCode()
			//you effectively tell the server that your request has finished and it can process it. 
			//If you want to send more data, you have to start a new request.
			logger.log(Level.INFO,"getResponseCode():"+connection.getResponseCode());
			logger.log(Level.INFO,"getResponseMessage():"+connection.getResponseMessage());
			logger.log(Level.INFO,"getErrorStream():"+connection.getErrorStream());

			returnString = outputString;
			connection.disconnect();			
			
		}catch(GitHubException e1){
			logger.log(Level.INFO, "GitHubException:"+String.valueOf(e1.getCode())+" "+ e1.getMessage() +" "+ e1.getErrorStream());
			
			GsonBuilder builder = new GsonBuilder();
	        builder.excludeFieldsWithoutExposeAnnotation();
	        final Gson gson = builder.create();
			String result = gson.toJson(e1);
			logger.log(Level.INFO, result);
			returnString = result;
		}catch (Exception e) {
			logger.log(Level.INFO, "Exception:"+e.getMessage());
			L2pLogger.logEvent(Event.SERVICE_MESSAGE,  e.getMessage());
			logger.printStackTrace(e);
			returnString = e.getMessage();
		} 
		
		return new HttpResponse(returnString, HttpURLConnection.HTTP_OK);
	}



	// //////////////////////////////////////////////////////////////////////////////////////
	// Methods required by the LAS2peer framework.
	// //////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Method for debugging purposes. Here the concept of restMapping validation
	 * is shown. It is important to check, if all annotations are correct and
	 * consistent. Otherwise the service will not be accessible by the
	 * WebConnector. Best to do it in the unit tests. To avoid being
	 * overlooked/ignored the method is implemented here and not in the test
	 * section.
	 * 
	 * @return true, if mapping correct
	 */
	public boolean debugMapping() {
		String XML_LOCATION = "./restMapping.xml";
		String xml = getRESTMapping();

		try {
			RESTMapper.writeFile(XML_LOCATION, xml);
		} catch (IOException e) {
			// write error to logfile and console
			logger.log(Level.SEVERE, e.toString(), e);
			// create and publish a monitoring message
			L2pLogger.logEvent(this, Event.SERVICE_ERROR, e.toString());
		}

		XMLCheck validator = new XMLCheck();
		ValidationResult result = validator.validate(xml);

		if (result.isValid()) {
			return true;
		}
		return false;
	}

	/**
	 * This method is needed for every RESTful application in LAS2peer. There is
	 * no need to change!
	 * 
	 * @return the mapping
	 */
	public String getRESTMapping() {
		String result = "";
		try {
			result = RESTMapper.getMethodsAsXML(this.getClass());
		} catch (Exception e) {
			// write error to logfile and console
			logger.log(Level.SEVERE, e.toString(), e);
			// create and publish a monitoring message
			L2pLogger.logEvent(this, Event.SERVICE_ERROR, e.toString());
		}
		return result;
	}

}
