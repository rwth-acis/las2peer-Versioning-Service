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
import javax.ws.rs.core.Response.Status;

import org.kohsuke.github.*;

import i5.las2peer.api.Context;

//import i5.las2peer.api.Service;
//import i5.las2peer.logging.L2pLogger;
//import i5.las2peer.logging.NodeObserver.Event;
//import i5.las2peer.p2p.ArtifactNotFoundException;
//import i5.las2peer.restMapper.annotations.ContentParam;
//import i5.las2peer.restMapper.annotations.ServicePath;
//import i5.las2peer.restMapper.HttpResponse;
//import i5.las2peer.restMapper.MediaType;
//import i5.las2peer.restMapper.RESTMapper;
//import i5.las2peer.restMapper.annotations.Version;
//import i5.las2peer.restMapper.tools.ValidationResult;
//import i5.las2peer.restMapper.tools.XMLCheck;
//import i5.las2peer.security.L2pSecurityException;
//import i5.las2peer.security.UserAgent;
//import io.swagger.annotations.*;

import i5.las2peer.logging.L2pLogger;
import i5.las2peer.logging.NodeObserver.Event;
import i5.las2peer.p2p.AgentNotKnownException;
import i5.las2peer.restMapper.RESTService;
import i5.las2peer.restMapper.annotations.ServicePath;
import i5.las2peer.security.UserAgent;
import i5.las2peer.webConnector.WebConnector;
import i5.las2peer.webConnector.client.ClientResponse;
import i5.las2peer.webConnector.client.MiniClient;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Contact;
import io.swagger.annotations.Info;
import io.swagger.annotations.License;
import io.swagger.annotations.SwaggerDefinition;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;

import java.net.HttpURLConnection;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import i5.las2peer.services.versioningService.exception.GitHubException;

@ServicePath("/template")
@Api
@SwaggerDefinition(info = @Info(
		title = "Co-Design Project Generation Service",
		version = "0.1",
		description = "For Co-Design project",
		termsOfService = "http://your-terms-of-service-url.com",
		contact = @Contact(
				name = "Yu-Wen, Huang",
				url = "provider.com",
				email = "yuhuang@dbis.rwth-aachen.de"),
		license = @License(
				name = "your software license name",
				url = "http://your-software-license-url.com")))

public class VersioningService extends RESTService {

	// Config Properties
	// test
	private String gitHubClientID;
	private String gitHubClientSecret;
	private String authorizationCode;

	private final L2pLogger logger = L2pLogger.getInstance(VersioningService.class.getName());

	@Override
	protected void initResources() {
		getResourceConfig().register(Resource.class);
	}

	public VersioningService() {
		// read and set properties values
		// IF THE SERVICE CLASS NAME IS CHANGED, THE PROPERTIES FILE NAME NEED
		// TO BE CHANGED TOO!
		setFieldValues();
	}

	public UserAgent getUserAgent() {
		return (UserAgent) Context.getCurrent().getMainAgent();
	}

	/*
	 * public Response getName(@PathParam("id") String id) {
	 * 
	 * long agentid = Long.parseLong(id); try { UserAgent fred = (UserAgent)
	 * Context.getCurrent().getAgent(agentid); String name =
	 * fred.getLoginName(); return
	 * Response.status(Status.OK).entity(name).build(); } catch
	 * (AgentNotKnownException e) { String error = "Agent not found"; return
	 * Response.status(Status.NOT_FOUND).entity(error).build(); } catch
	 * (Exception e) { String error = "Internal error"; return
	 * Response.status(Status.INTERNAL_SERVER_ERROR).entity(error).build(); } }
	 */

	// //////////////////////////////////////////////////////////////////////////////////////
	// Service methods.
	// //////////////////////////////////////////////////////////////////////////////////////
	@Path("/") // this is the root resource
	public static class Resource {
		// get access to the service class
		private VersioningService service = (VersioningService) Context.getCurrent().getService();

		// instantiate the logger class
		private final L2pLogger logger = L2pLogger.getInstance(VersioningService.class.getName());

		private static final String HTTP_ADDRESS = "http://127.0.0.1";
		private static final int HTTP_PORT = WebConnector.DEFAULT_HTTP_PORT;
		
		/**
		 * Test of a basic get function.
		 * 
		 * @return HttpResponse with the returnString
		 */
		@GET
		@Path("/properties")
		@Produces(MediaType.TEXT_PLAIN)
		@ApiOperation(value = "REPLACE THIS WITH AN APPROPRIATE FUNCTION NAME", notes = "REPLACE THIS WITH YOUR NOTES TO THE FUNCTION")
		@ApiResponses(value = { @ApiResponse(code = HttpURLConnection.HTTP_OK, message = "Test success"),
				@ApiResponse(code = HttpURLConnection.HTTP_UNAUTHORIZED, message = "Unauthorized") })
		public Response getProperties() {
			String returnString = service.gitHubClientID;
			logger.log(Level.INFO, "test properties values gitHubClientID:" + service.gitHubClientID);

			logger.log(Level.INFO, "Context.getCurrent().getMainAgent().getId():" + Context.getCurrent().getMainAgent().getId());

			return Response.ok().entity(returnString).build();
		}

		/**
		 * Get GitHub return code, in order to exchange access token in the
		 * future
		 * 
		 * @return HttpResponse with the returnString
		 */
		@GET
		@Path("/callbacktest")
		@Produces(MediaType.TEXT_PLAIN)
		@ApiResponses(value = {
				@ApiResponse(code = HttpURLConnection.HTTP_OK, message = "REPLACE THIS WITH YOUR OK MESSAGE"),
				@ApiResponse(code = HttpURLConnection.HTTP_UNAUTHORIZED, message = "Unauthorized") })
		@ApiOperation(value = "REPLACE THIS WITH AN APPROPRIATE FUNCTION NAME", notes = "Example method that returns a phrase containing the received input.")
		public Response tryCallback(@QueryParam("code") String code) {
			String returnString = "";
			System.out.println("code parameter = " + code);
			returnString += code;
			// return new HttpResponse(returnString, HttpURLConnection.HTTP_OK);
			return Response.ok().entity(returnString).build();
		}

		@GET
		@Path("/createagent")
		@Produces(MediaType.APPLICATION_JSON)
		@ApiResponses(value = {
				@ApiResponse(code = HttpURLConnection.HTTP_OK, message = "Successfully get repositories list."),
				@ApiResponse(code = HttpURLConnection.HTTP_UNAUTHORIZED, message = "Unauthorized") })
		@ApiOperation(value = "REPLACE THIS WITH AN APPROPRIATE FUNCTION NAME", notes = "Example method that returns a phrase containing the received input.")
		public Response createUserAgent(
				@QueryParam("name") String userName,
				@QueryParam("email") String userEmail) {
			String returnString = "";
			try {
				UserAgent userAgent = UserAgent.createUserAgent(userName);
				userAgent.unlockPrivateKey(userName); // agent must be unlocked in order to be stored
				userAgent.setLoginName(userName);
				userAgent.setEmail(userEmail);

				Context.getCurrent().getLocalNode().storeAgent(userAgent);
				logger.log(Level.INFO, "userAgent.getID():" + userAgent.getId());
				returnString = "user agent name:" + userName+", id:"+ userAgent.getId();
				return Response.ok().entity(returnString).build();
			}
			catch (Exception e) {
				// TODO 
				// thrown on duplicate
				// in this case, the agent has been stored, but login name and email were not set
				// simply change login name and email and call updateAgent(usr)
				logger.log(Level.INFO, "Create agent fail:"+e.toString());
				return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Internal Server Error").build();
			}		
			
		}
		@GET
		@Path("/getcurrentagent")
		@Produces(MediaType.APPLICATION_JSON)
		@ApiResponses(value = {
				@ApiResponse(code = HttpURLConnection.HTTP_OK, message = "Successfully get repositories list."),
				@ApiResponse(code = HttpURLConnection.HTTP_UNAUTHORIZED, message = "Unauthorized") })
		@ApiOperation(value = "REPLACE THIS WITH AN APPROPRIATE FUNCTION NAME", notes = "Example method that returns a phrase containing the received input.")
		public Response getCurrentAgent() {
			String returnString = "";
			try {
				
				// the request header needs to be written in frontend request
				// such as Authorization: Basic BASE64([id or login name]:[passphrase])
				// see https://github.com/rwth-acis/las2peer-Template-Project/wiki/WebConnector%3A-Request-Authentication
				// but here does'n need to use @HeaderParam("Authorization"), las2peer will handle automatically
				
				// get the main agent (= current log in agent)
				UserAgent userAgent = (UserAgent) Context.getCurrent().getMainAgent();
				long agendId = userAgent.getId();
				String agentName = userAgent.getLoginName();
				
				//logger.log(Level.INFO, "Context.getCurrent().getMainAgent().getId():" + agentid);
				logger.log(Level.INFO, "UserAgent Id:" + agendId);
				logger.log(Level.INFO, "UserAgent getLoginName():" + agentName);

				returnString = agentName;
				return Response.ok().entity(returnString).build();
			}
			catch (Exception e) {
				return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Internal Server Error").build();
			}		
			
		}

		/**
		 * List repositories for the specified organization See
		 * https://developer.github.com/v3/repos/#list-organization-repositories
		 * 
		 * @param accessToken GitHub access_token
		 * @param orgName GitHub organization name
		 * @return HttpResponse with the returnString
		 */
		@GET
		@Path("/projects")
		@Produces(MediaType.APPLICATION_JSON)
		@ApiResponses(value = {
				@ApiResponse(code = HttpURLConnection.HTTP_OK, message = "Successfully get repositories list."),
				@ApiResponse(code = HttpURLConnection.HTTP_UNAUTHORIZED, message = "Unauthorized") })
		@ApiOperation(value = "REPLACE THIS WITH AN APPROPRIATE FUNCTION NAME", notes = "Example method that returns a phrase containing the received input.")
		public Response showAllRepos(
				@HeaderParam("token") String accessToken,
				@QueryParam("org") String orgName) {
			String returnString = "";
			try {
				URL url = new URL("https://api.github.com/orgs/" + orgName + "/repos");
				HttpURLConnection connection = (HttpURLConnection) url.openConnection();
				connection.setRequestMethod("GET");

				logger.log(Level.INFO, "projects token:" + accessToken);

				if (accessToken != null) {
					if ((!accessToken.equals("undefined")) && (accessToken.length() != 0)) {
						connection.setRequestProperty("Authorization", "token " + accessToken);
					}
				}

				connection.setUseCaches(false);
				connection.setDoInput(true);
				connection.setDoOutput(true);
				connection.setConnectTimeout(7000);

				logger.log(Level.INFO, "request url:" + url.toString());

				// if failed
				if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
					BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
					String line;
					String errorString = "";
					while ((line = reader.readLine()) != null) {
						errorString = errorString + line;
					}
					throw new GitHubException(
							connection.getResponseCode(),
							connection.getResponseMessage(),
							errorString);
				}

				String outputString = "";
				BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
				String line;
				reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
				while ((line = reader.readLine()) != null) {
					outputString = outputString + line;
				}
				reader.close();
				returnString = outputString;
			} catch (GitHubException e1) {
				logger.log(Level.INFO, "GitHubException:" + String.valueOf(e1.getCode()) + " " + e1.getMessage() + " "
						+ e1.getErrorStream());
				GsonBuilder builder = new GsonBuilder();
				builder.excludeFieldsWithoutExposeAnnotation();
				final Gson gson = builder.create();
				String result = gson.toJson(e1);
				logger.log(Level.INFO, result);
				returnString = result;
			} catch (Exception e) {
				logger.log(Level.INFO, "Exception:" + e.getMessage());
				L2pLogger.logEvent(this, Event.SERVICE_ERROR, e.getMessage());

				logger.printStackTrace(e);
				returnString = e.getMessage();
			}
			return Response.ok().entity(returnString).build();
		}

		/**
		 * Get one specific repository information See
		 * https://developer.github.com/v3/repos/#get
		 * 
		 * @param accessToken GitHub access_token
		 * @param orgName GitHub organization name
		 * @param repoName GitHub repository name
		 * @return HttpResponse with the returnString
		 */
		@GET
		@Path("/projectinfo")
		@Produces(MediaType.APPLICATION_JSON)
		@ApiResponses(value = {
				@ApiResponse(code = HttpURLConnection.HTTP_OK, message = "Successfully get repositories list."),
				@ApiResponse(code = HttpURLConnection.HTTP_UNAUTHORIZED, message = "Unauthorized") })
		@ApiOperation(value = "REPLACE THIS WITH AN APPROPRIATE FUNCTION NAME", notes = "Example method that returns a phrase containing the received input.")
		public Response showProjectInfo(
				@HeaderParam("token") String accessToken,
				@QueryParam("org") String orgName,
				@QueryParam("repo") String repoName) {
			String returnString = "";
			try {
				// example url:
				// https://api.github.com/repos/Co-Design-Platform/panda;
				URL url = new URL("https://api.github.com/repos/" + orgName + "/" + repoName);
				HttpURLConnection connection = (HttpURLConnection) url.openConnection();
				connection.setRequestMethod("GET");
				logger.log(Level.INFO, "accessToken token:" + accessToken);

				if ((!accessToken.equals("undefined")) && (accessToken.length() != 0)) {
					connection.setRequestProperty("Authorization", "token " + accessToken);
				}
				connection.setUseCaches(false);
				connection.setDoInput(true);
				connection.setDoOutput(true);
				connection.setConnectTimeout(7000);

				logger.log(Level.INFO, "request url:" + url.toString());

				String outputString = "";
				BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
				String line;
				reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
				while ((line = reader.readLine()) != null) {
					outputString = outputString + line;
				}
				reader.close();
				returnString = outputString;
				return Response.ok().entity(returnString).build();
			} catch (Exception e) {
				logger.log(Level.SEVERE, "showAllRepos problem:", e);
				logger.printStackTrace(e);
				return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Internal Server Error").build();

			}
		}

		/**
		 * Get one specific repository's branch information See
		 * https://developer.github.com/v3/repos/branches/#get-branch
		 * 
		 * @param accessToken GitHub access_token
		 * @param orgName GitHub organization name
		 * @param repoName GitHub repository name
		 * @return HttpResponse with the returnString
		 */
		@GET
		@Path("/projectbranchinfo")
		@Produces(MediaType.APPLICATION_JSON)
		@ApiResponses(value = {
				@ApiResponse(code = HttpURLConnection.HTTP_OK, message = "Successfully get repositories list."),
				@ApiResponse(code = HttpURLConnection.HTTP_UNAUTHORIZED, message = "Unauthorized") })
		@ApiOperation(value = "REPLACE THIS WITH AN APPROPRIATE FUNCTION NAME", notes = "Example method that returns a phrase containing the received input.")
		public Response showProjectBranchInfo(
				@HeaderParam("token") String accessToken,
				@QueryParam("org") String orgName,
				@QueryParam("repo") String repoName) {
			String returnString = "";
			try {
				// example url:
				// https://api.github.com/repos/Co-Design-Platform/panda/branches
				URL url = new URL("https://api.github.com/repos/" + orgName + "/" + repoName + "/" + "branches");
				HttpURLConnection connection = (HttpURLConnection) url.openConnection();
				connection.setRequestMethod("GET");
				logger.log(Level.INFO, "projects token:" + accessToken);

				if ((!accessToken.equals("undefined")) && (accessToken.length() != 0)) {
					connection.setRequestProperty("Authorization", "token " + accessToken);
				}
				connection.setUseCaches(false);
				connection.setDoInput(true);
				connection.setDoOutput(true);
				connection.setConnectTimeout(7000);

				logger.log(Level.INFO, "request url:" + url.toString());

				String outputString = "";
				BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
				String line;
				reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
				while ((line = reader.readLine()) != null) {
					outputString = outputString + line;
				}
				reader.close();
				returnString = outputString;
				return Response.ok().entity(returnString).build();

			} catch (Exception e) {
				logger.log(Level.SEVERE, "showProjectBranchInfo problem:", e);
				logger.printStackTrace(e);
				return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Internal Server Error").build();

			}
		}

		/**
		 * Get components of a repository See
		 * https://developer.github.com/v3/repos/contents/#get-contents
		 * 
		 * @param accessToken GitHub access_token
		 * @param orgName GitHub organization name
		 * @param repoName GitHub repository name
		 * @param branchName GitHub repository brahch name
		 * @return HttpResponse with the returnString
		 */
		@GET
		@Path("/components")
		@Produces(MediaType.APPLICATION_JSON)
		@ApiResponses(value = {
				@ApiResponse(code = HttpURLConnection.HTTP_OK, message = "Successfully get repositories list."),
				@ApiResponse(code = HttpURLConnection.HTTP_UNAUTHORIZED, message = "Unauthorized") })
		@ApiOperation(value = "REPLACE THIS WITH AN APPROPRIATE FUNCTION NAME", notes = "Example method that returns a phrase containing the received input.")
		public Response showProjectComponents(
				@HeaderParam("token") String accessToken,
				@QueryParam("org") String orgName,
				@QueryParam("repo") String repoName,
				@QueryParam("branch") String branchName) {
			String returnString = "";
			try {
				// GET /repos/:owner/:repo/contents/:path
				// example url:
				// https://api.github.com/repos/Co-Design-Platform/panda/contents?ref=master
				URL url = new URL(
						"https://api.github.com/repos/" + orgName + "/" + repoName + "/contents/?ref=" + branchName);
				HttpURLConnection connection = (HttpURLConnection) url.openConnection();
				connection.setRequestMethod("GET");
				logger.log(Level.INFO, "projects token:" + accessToken);

				if ((!accessToken.equals("undefined")) && (accessToken.length() != 0)) {
					connection.setRequestProperty("Authorization", "token " + accessToken);
				}
				connection.setUseCaches(false);
				connection.setDoInput(true);
				connection.setDoOutput(true);
				connection.setConnectTimeout(7000);

				logger.log(Level.INFO, "request url:" + url.toString());

				String outputString = "";
				BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
				String line;
				reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
				while ((line = reader.readLine()) != null) {
					outputString = outputString + line;
				}
				reader.close();
				logger.log(Level.INFO, "request url:" + url.toString());
				// logger.log(Level.INFO,outputString);

				/*
				 * the original download_url from GitHub is like:
				 * https://raw.githubusercontent.com/Co-Design-Platform/panda/
				 * master/bear.svg but this svg cannot be shown on browser
				 * directly within <img> because GitHub sends text/plain header
				 * see
				 * http://stackoverflow.com/questions/13808020/include-an-svg-
				 * hosted-on-github-in-markdown and
				 * https://github.com/isaacs/github/issues/316 so we need to
				 * replace download_url in order to show svg on browser
				 */

				String newOutputString = outputString.replace("raw.githubusercontent.com", "rawgit.com");

				// logger.log(Level.INFO,"\n");
				// logger.log(Level.INFO,"NEW outputString: "+newOutputString);

				returnString = newOutputString;
				return Response.ok().entity(returnString).build();

			} catch (Exception e) {
				logger.log(Level.SEVERE, "showAllRepos problem:", e);
				logger.printStackTrace(e);
				// return new HttpResponse(e.getMessage(), 500);
				return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();

			}
		}

		/**
		 * Get one specific file information
		 * 
		 * @param accessToken GitHub access_token
		 * @param orgName GitHub organization name
		 * @param repoName GitHub repository name
		 * @return HttpResponse with the returnString
		 */
		@GET
		@Path("/componentinfo")
		@Produces(MediaType.APPLICATION_JSON)
		@ApiResponses(value = {
				@ApiResponse(code = HttpURLConnection.HTTP_OK, message = "Successfully get repositories list."),
				@ApiResponse(code = HttpURLConnection.HTTP_UNAUTHORIZED, message = "Unauthorized") })
		@ApiOperation(value = "REPLACE THIS WITH AN APPROPRIATE FUNCTION NAME", notes = "Example method that returns a phrase containing the received input.")
		public Response getComponentInfo(
				@HeaderParam("token") String accessToken,
				@QueryParam("org") String orgName, 
				@QueryParam("repo") String repoName,
				@QueryParam("branch") String branchName, 
				@QueryParam("path") String pathName) {
			String returnString = "";
			try {
				// example url:
				// https://api.github.com/repos/Co-Design-Platform/panda/contents/Sad_panda.svg?ref=master
				URL url = new URL("https://api.github.com/repos/" + orgName + "/" + repoName + "/contents/" + pathName
						+ "?ref=" + branchName);
				HttpURLConnection connection = (HttpURLConnection) url.openConnection();
				connection.setRequestMethod("GET");
				logger.log(Level.INFO, "projects token:" + accessToken);

				if ((!accessToken.equals("undefined")) && (accessToken.length() != 0)) {
					connection.setRequestProperty("Authorization", "token " + accessToken);
				}
				connection.setUseCaches(false);
				connection.setDoInput(true);
				connection.setDoOutput(true);
				connection.setConnectTimeout(7000);

				logger.log(Level.INFO, "request url:" + url.toString());

				String outputString = "";
				BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
				String line;
				reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
				while ((line = reader.readLine()) != null) {
					outputString = outputString + line;
				}
				reader.close();
				returnString = outputString;
				return Response.ok().entity(returnString).build();
			} catch (Exception e) {
				logger.log(Level.SEVERE, "getComponentInfo problem:", e);
				logger.printStackTrace(e);
				return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();

			}
		}

		/**
		 * Get SVG content of a single SVG component
		 * 
		 * @param svgUrl the download_url from GitHub
		 * @return HttpResponse with the returnString
		 */
		@GET
		@Path("/getsvgcontent")
		// @Produces(MediaType.APPLICATION_XML)
		@Produces("image/svg+xml")
		@ApiResponses(value = {
				@ApiResponse(code = HttpURLConnection.HTTP_OK, message = "REPLACE THIS WITH YOUR OK MESSAGE"),
				@ApiResponse(code = HttpURLConnection.HTTP_UNAUTHORIZED, message = "Unauthorized") })
		@ApiOperation(value = "REPLACE THIS WITH AN APPROPRIATE FUNCTION NAME", notes = "Example method that returns a phrase containing the received input.")
		public Response getSvgContent(@QueryParam("svgurl") String svgUrl) {
			String returnString = "";
			try {
				// example
				// url:https://raw.githubusercontent.com/Co-Design-Platform/panda/master/Taipei_Zoo.png
				URL url = new URL(svgUrl);
				HttpURLConnection connection = (HttpURLConnection) url.openConnection();
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

				// HttpResponse result = new
				// HttpResponse(outputString,HttpURLConnection.HTTP_OK);
				// result.setHeader("content-type", "image/svg+xml");
				// return result;
				
				returnString = outputString;
				return Response.ok().entity(returnString).build();

			} catch (Exception e) {
				logger.log(Level.SEVERE, "getSvgContent error:", e);
				logger.printStackTrace(e);
				return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();

			}
		}

		/**
		 * Get GitHub authenticated user see
		 * https://developer.github.com/v3/users/#get-the-authenticated-user
		 * 
		 * @param accessToken
		 * @return HttpResponse with the returnString
		 */
		@GET
		@Path("/getCurrentGitHubUser")
		@Produces(MediaType.APPLICATION_JSON)
		@ApiResponses(value = {
				@ApiResponse(code = HttpURLConnection.HTTP_OK, message = "REPLACE THIS WITH YOUR OK MESSAGE"),
				@ApiResponse(code = HttpURLConnection.HTTP_UNAUTHORIZED, message = "Unauthorized") })
		@ApiOperation(value = "REPLACE THIS WITH AN APPROPRIATE FUNCTION NAME", notes = "Example method that returns a phrase containing the received input.")
		public Response getCurrentGitHubUser(
				@HeaderParam("token") String accessToken) {
			String returnString;
			try {
				URL url = new URL("https://api.github.com/user");
				HttpURLConnection connection = (HttpURLConnection) url.openConnection();
				connection.setRequestMethod("GET");

				logger.log(Level.INFO, "url:" + url + ", token:" + accessToken);

				if (accessToken != null) {
					if ((!accessToken.equals("undefined")) && (accessToken.length() != 0)) {
						connection.setRequestProperty("Authorization", "token " + accessToken);
					}
				}

				connection.setUseCaches(false);
				connection.setDoInput(true);
				connection.setDoOutput(true);
				connection.setConnectTimeout(7000);

				// if is failed
				if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
					BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
					String line;
					String errorString = "";
					while ((line = reader.readLine()) != null) {
						errorString = errorString + line;
					}
					throw new GitHubException(
							connection.getResponseCode(),
							connection.getResponseMessage(),
							errorString);
				} else {
					String outputString = "";
					BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
					String line;
					// reader = new BufferedReader(new
					// InputStreamReader(connection.getInputStream()));
					while ((line = reader.readLine()) != null) {
						outputString = outputString + line;
					}
					reader.close();
					returnString = outputString;
					return Response.ok().entity(returnString).build();
				}
			} catch (GitHubException e1) {
				logger.log(Level.INFO, "GitHubException:" + String.valueOf(e1.getCode()) + " " + e1.getMessage() + " "
						+ e1.getErrorStream());

				GsonBuilder builder = new GsonBuilder();
				builder.excludeFieldsWithoutExposeAnnotation();
				final Gson gson = builder.create();
				String result = gson.toJson(e1);
				logger.log(Level.INFO, result);
				returnString = result;
				return Response.status(e1.getCode()).entity(returnString).build();
			} catch (Exception e) {
				logger.log(Level.SEVERE, "getCurrentGitHubUser error:", e);
				logger.printStackTrace(e);
				return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
			}
		}

		/**
		 * 
		 * 
		 * /** Get use code to get GitHub access_token
		 * 
		 * @return HttpResponse with the returnString
		 */
		@GET
		@Path("/githubaccess")
		@Produces(MediaType.APPLICATION_JSON)
		@ApiResponses(value = { @ApiResponse(code = HttpURLConnection.HTTP_OK, message = "GitHub access_token"),
				@ApiResponse(code = HttpURLConnection.HTTP_UNAUTHORIZED, message = "Unauthorized") })
		public Response getGitHubToken(@QueryParam("code") String code) {
			try {
				String returnString = null;

				if (code == null) {
					/*
					 * URI url = new URI(
					 * "https://github.com/login/oauth/authorize?client_id="+
					 * service.gitHubClientID);
					 * return(Response.temporaryRedirect(url).build());
					 */ // how to redirect directly? Response type, not
						// HttpResponse
					returnString = "Sorry. Please Sign In GitHub.";
					// return new HttpResponse (returnString,
					// HttpURLConnection.HTTP_OK);
					return Response.ok().entity(returnString).build();
				} else {
					URL url = new URL("https://github.com/login/oauth/access_token?client_id=" + service.gitHubClientID
					// + "&redirect_uri=" + redirectURI
							+ "&client_secret=" + service.gitHubClientSecret + "&code=" + code);

					HttpURLConnection connection = (HttpURLConnection) url.openConnection();
					connection.setRequestMethod("POST");
					connection.setConnectTimeout(20000);

					String outputString = "";
					BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
					String line;
					while ((line = reader.readLine()) != null) {
						outputString = outputString + line;
					}
					logger.log(Level.INFO, "request url:" + url.toString());
					logger.log(Level.INFO, outputString);
					String accessToken = null;

					/*
					 * If the verification code you pass is incorrect, expired,
					 * or doesn't match what you received in the first request
					 * for authorization you will receive this error.{ "error":
					 * "bad_verification_code", "error_description":
					 * "The code passed is incorrect or expired.", "error_uri":
					 * "https://developer.github.com/v3/oauth/#bad-verification-code"
					 * } To solve this error, start the OAuth process over from
					 * the beginning and get a new code.
					 */
					if (outputString.indexOf("bad_verification_code") != -1) {
						returnString = "request:" + url.toString()
								+ ": Authorization fail(incorrect/expire/not match), please authorize again.";
					} else if (outputString.indexOf("access_token") != -1) {
						accessToken = outputString.substring(13, outputString.indexOf("&"));
						returnString = accessToken;
					}
					logger.log(Level.INFO, "accessToken: " + accessToken);
					Gson gson = new Gson();
					returnString = gson.toJson(returnString);
					return Response.ok().entity(returnString).build();
				}
			} catch (Exception e) {
				logger.log(Level.SEVERE, "getGitHubToken problem:", e);
				logger.printStackTrace(e);
				// return new HttpResponse(e.getMessage(), 500);
				return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();

			}
		}

		/**
		 * This method allows to create a new project(a GitHub repository).
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
				@ApiResponse(code = HttpURLConnection.HTTP_INTERNAL_ERROR, message = "Internal server problems") })
		public Response createProject(
				@HeaderParam("token") String accessToken,
				String project) {
			String returnString = "";
			try {

				logger.log(Level.INFO, "Create a new repo");
				logger.log(Level.INFO, "access token:" + accessToken);
				logger.log(Level.INFO, project);

				// JsonObject jobj = new Gson().fromJson(project,
				// JsonObject.class);
				// logger.log(Level.INFO, jobj.get("name").toString());
				// logger.log(Level.INFO, jobj.get("description").toString());
				// logger.log(Level.INFO, jobj.toString());

				URL url = new URL("https://api.github.com/orgs/Co-Design-Platform/repos");

				HttpURLConnection connection = (HttpURLConnection) url.openConnection();
				connection.setRequestProperty("Authorization", "token " + accessToken);
				// connection.setRequestProperty ("Authorization", accessToken);

				connection.setRequestMethod("POST");
				connection.setRequestProperty("Content-Type", "application/json");
				connection.setConnectTimeout(20000);
				connection.setDoOutput(true);

				logger.log(Level.INFO, "request url:" + connection.getURL().toString());

				OutputStream os = connection.getOutputStream();
				os.write(project.getBytes("UTF-8"));
				os.flush();

				// if create a project is failed
				if (connection.getResponseCode() != HttpURLConnection.HTTP_CREATED) {
					// throw new RuntimeException("code : "
					// + connection.getResponseCode()+",msg:
					// "+connection.getResponseMessage());
					BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
					String line;
					String errorString = "";
					while ((line = reader.readLine()) != null) {
						errorString = errorString + line;
					}
					throw new GitHubException(
							connection.getResponseCode(),
							connection.getResponseMessage(),
							errorString);
				}

				String outputString = "";
				BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
				String line;
				while ((line = reader.readLine()) != null) {
					outputString = outputString + line;
				}

				// the response header fields
				logger.log(Level.INFO, "getHeaderFields():" + connection.getHeaderFields().toString());

				// when you call server.getResponseCode()
				// you effectively tell the server that your request has
				// finished and it can process it.
				// If you want to send more data, you have to start a new
				// request.
				logger.log(Level.INFO, "getResponseCode():" + connection.getResponseCode());
				logger.log(Level.INFO, "getResponseMessage():" + connection.getResponseMessage());
				logger.log(Level.INFO, "getErrorStream():" + connection.getErrorStream());

				returnString = outputString;
				connection.disconnect();

			} catch (GitHubException e1) {
				logger.log(Level.INFO, "GitHubException:" + String.valueOf(e1.getCode()) + " " + e1.getMessage() + " "
						+ e1.getErrorStream());

				GsonBuilder builder = new GsonBuilder();
				builder.excludeFieldsWithoutExposeAnnotation();
				final Gson gson = builder.create();
				String result = gson.toJson(e1);
				logger.log(Level.INFO, result);
				returnString = result;
			} catch (Exception e) {
				logger.log(Level.INFO, "Exception:" + e.getMessage());
				L2pLogger.logEvent(this, Event.SERVICE_ERROR, e.getMessage());
				logger.printStackTrace(e);
				returnString = e.getMessage();
			}
			return Response.ok().entity(returnString).build();
		}

		/**
		 * This method allows to create a new file on GitHub see
		 * https://developer.github.com/v3/repos/contents/#create-a-file
		 * 
		 * @param accessToken GitHub access_token
		 * @param orgName GitHub organization name
		 * @param repoName GitHub repository name
		 * @param pathName GitHub filepath
		 * @return Response with the created project as a JSON object.
		 */
		@POST
		@Path("/createfile")
		@Produces(MediaType.APPLICATION_JSON)
		@ApiOperation(value = "This method allows to create a new file on GitHub")
		@ApiResponses(value = {
				@ApiResponse(code = HttpURLConnection.HTTP_CREATED, message = "Returns the created project"),
				@ApiResponse(code = HttpURLConnection.HTTP_UNAUTHORIZED, message = "Unauthorized"),
				@ApiResponse(code = HttpURLConnection.HTTP_INTERNAL_ERROR, message = "Internal server problems") })
		public Response createFile(
				@HeaderParam("token") String accessToken,
				@QueryParam("org") String orgName,
				@QueryParam("repo") String repoName,
				@QueryParam("path") String pathName, String body) {
			String returnString = "";
			try {

				URL url = new URL("https://api.github.com/repos/" + orgName + "/" + repoName + "/contents/" + pathName);

				HttpURLConnection connection = (HttpURLConnection) url.openConnection();
				connection.setRequestProperty("Authorization", "token " + accessToken);

				connection.setRequestMethod("PUT");
				connection.setRequestProperty("Content-Type", "application/json");
				connection.setConnectTimeout(20000);
				connection.setDoOutput(true);
				logger.log(Level.INFO, "request url:" + connection.getURL().toString());

				// body needs to contains branch & commit message & file content
				OutputStream os = connection.getOutputStream();
				os.write(body.getBytes());
				os.flush();

				// if create a file is failed
				if (connection.getResponseCode() != HttpURLConnection.HTTP_CREATED) {
					// throw new RuntimeException("code : "
					// + connection.getResponseCode()+",msg:
					// "+connection.getResponseMessage());
					BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
					String line;
					String errorString = "";
					while ((line = reader.readLine()) != null) {
						errorString = errorString + line;
					}
					throw new GitHubException(
							connection.getResponseCode(),
							connection.getResponseMessage(),
							errorString);
				}

				String outputString = "";
				BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
				String line;
				while ((line = reader.readLine()) != null) {
					outputString = outputString + line;
				}

				// the response header fields
				logger.log(Level.INFO, "getHeaderFields():" + connection.getHeaderFields().toString());
				logger.log(Level.INFO, "getResponseCode():" + connection.getResponseCode());
				logger.log(Level.INFO, "getResponseMessage():" + connection.getResponseMessage());
				logger.log(Level.INFO, "getErrorStream():" + connection.getErrorStream());

				returnString = outputString;
				connection.disconnect();
				return Response.ok().entity(returnString).build();

			} catch (GitHubException e1) {
				logger.log(Level.INFO, "GitHubException:" + String.valueOf(e1.getCode()) + " " + e1.getMessage() + " "
						+ e1.getErrorStream());
				GsonBuilder builder = new GsonBuilder();
				builder.excludeFieldsWithoutExposeAnnotation();
				final Gson gson = builder.create();
				String result = gson.toJson(e1);
				logger.log(Level.INFO, result);
				returnString = result;
				return Response.status(e1.getCode()).entity(returnString).build();

			} catch (Exception e) {
				logger.log(Level.INFO, "Exception:" + e.getMessage());

				L2pLogger.logEvent(this, Event.SERVICE_ERROR, e.getMessage());

				logger.printStackTrace(e);
				returnString = e.getMessage();
				return Response.status(Status.INTERNAL_SERVER_ERROR).entity(returnString).build();

			}
		}

		/**
		 * This method allows to update a new file on GitHub see
		 * https://developer.github.com/v3/repos/contents/#update-a-file
		 * 
		 * @param accessToken GitHub access_token
		 * @param orgName GitHub organization name
		 * @param repoName GitHub repository name
		 * @param pathName GitHub filepath
		 * @return Response with the created project as a JSON object.
		 */
		@POST
		@Path("/updatefile")
		@Produces(MediaType.APPLICATION_JSON)
		@ApiOperation(value = "This method allows to create a new file on GitHub")
		@ApiResponses(value = {
				@ApiResponse(code = HttpURLConnection.HTTP_CREATED, message = "Returns the created project"),
				@ApiResponse(code = HttpURLConnection.HTTP_UNAUTHORIZED, message = "Unauthorized"),
				@ApiResponse(code = HttpURLConnection.HTTP_INTERNAL_ERROR, message = "Internal server problems") })
		public Response updateFile(
				@HeaderParam("token") String accessToken,
				@QueryParam("org") String orgName,
				@QueryParam("repo") String repoName,
				@QueryParam("path") String pathName, String body) {
			String returnString = "";
			try {
				URL url = new URL("https://api.github.com/repos/" + orgName + "/" + repoName + "/contents/" + pathName);

				HttpURLConnection connection = (HttpURLConnection) url.openConnection();
				connection.setRequestProperty("Authorization", "token " + accessToken);
				connection.setRequestMethod("PUT");
				connection.setRequestProperty("Content-Type", "application/json");
				connection.setConnectTimeout(20000);
				connection.setDoOutput(true);
				logger.log(Level.INFO, "request url:" + connection.getURL().toString());
				// https://api.github.com/repos/Co-Design-Platform/panda/contents/bear.svg

				// body needs to contains sha & branch & commit message & file
				// content
				OutputStream os = connection.getOutputStream();
				os.write(body.getBytes());
				os.flush();

				// if update a file is failed
				if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
					logger.log(Level.INFO, "Update Error, ResponseCode is not 200");
					logger.log(Level.INFO, "getHeaderFields():" + connection.getHeaderFields().toString());
					logger.log(Level.INFO, "getResponseCode():" + connection.getResponseCode());
					logger.log(Level.INFO, "getResponseMessage():" + connection.getResponseMessage());
					logger.log(Level.INFO, "getErrorStream():" + connection.getErrorStream());

					BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
					String line;
					String errorString = "";
					while ((line = reader.readLine()) != null) {
						errorString = errorString + line;
					}
					throw new GitHubException(
							connection.getResponseCode(),
							connection.getResponseMessage(),
							errorString);
				}

				String outputString = "";
				BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
				String line;
				while ((line = reader.readLine()) != null) {
					outputString = outputString + line;
				}
				logger.log(Level.INFO, outputString);

				// the response header fields
				logger.log(Level.INFO, "getHeaderFields():" + connection.getHeaderFields().toString());
				logger.log(Level.INFO, "getResponseCode():" + connection.getResponseCode());
				logger.log(Level.INFO, "getResponseMessage():" + connection.getResponseMessage());
				logger.log(Level.INFO, "getErrorStream():" + connection.getErrorStream());

				returnString = outputString;
				connection.disconnect();
				return Response.ok().entity(returnString).build();
			} catch (GitHubException e1) {
				logger.log(Level.INFO, "GitHubException:" + String.valueOf(e1.getCode()) + " " + e1.getMessage() + " "
						+ e1.getErrorStream());
				GsonBuilder builder = new GsonBuilder();
				builder.excludeFieldsWithoutExposeAnnotation();
				final Gson gson = builder.create();
				String result = gson.toJson(e1);
				logger.log(Level.INFO, result);
				returnString = result;
				return Response.status(e1.getCode()).entity(returnString).build();

			} catch (Exception e) {
				logger.log(Level.INFO, "Exception:" + e.getMessage());
				logger.printStackTrace(e);
				L2pLogger.logEvent(this, Event.SERVICE_ERROR, e.getMessage());
				returnString = e.getMessage();
				return Response.status(Status.INTERNAL_SERVER_ERROR).entity(returnString).build();

			}
		}

		/**
		 * This method allows to create a new file on GitHub see
		 * https://developer.github.com/v3/repos/contents/#create-a-file use lib
		 * http://github-api.kohsuke.org/
		 * 
		 * @param accessToken GitHub access_token
		 * @param orgName GitHub organization name
		 * @param repoName GitHub repository name
		 * @param pathName GitHub filepath
		 * @param commitMessage GitHub commit message
		 * @return Response with the created project as a JSON object.
		 */
		@POST
		@Path("/createfile2")
		@Produces(MediaType.APPLICATION_JSON)
		@ApiOperation(value = "This method allows to create a new file on GitHub")
		@ApiResponses(value = {
				@ApiResponse(code = HttpURLConnection.HTTP_CREATED, message = "Returns the created project"),
				@ApiResponse(code = HttpURLConnection.HTTP_UNAUTHORIZED, message = "Unauthorized"),
				@ApiResponse(code = HttpURLConnection.HTTP_INTERNAL_ERROR, message = "Internal server problems") })
		public Response createFileUseLib(
				@HeaderParam(value = HttpHeaders.AUTHORIZATION) String accessToken,
				@QueryParam("org") String orgName,
				@QueryParam("repo") String repoName,
				@QueryParam("path") String pathName,
				@QueryParam("msg") String commitMessage, String fileContent) {
			String returnString = "";
			try {
				GitHub github = GitHub.connectUsingOAuth(accessToken);
				GHOrganization org = github.getOrganization(orgName);
				GHRepository repo = org.getRepository(repoName);
				logger.log(Level.INFO, "org: " + org.getName());
				logger.log(Level.INFO, "repo: " + repo.getName() + ", owner name:" + repo.getOwnerName()
						+ ", owner.email:" + repo.getOwner().getEmail());

				GHContentUpdateResponse updateResponse = repo.createContent(fileContent, commitMessage, pathName);
				String responseContentSha = updateResponse.getContent().getSha();
				logger.log(Level.INFO, "updateResponse:" + updateResponse.getContent().isFile());
				logger.log(Level.INFO, "SHA:" + responseContentSha);
				returnString = updateResponse.toString();
			} catch (Exception e) {
				logger.log(Level.INFO, "Exception:" + e.getMessage());
				L2pLogger.logEvent(this, Event.SERVICE_ERROR, e.getMessage());
				logger.printStackTrace(e);
				returnString = e.getMessage();
			}

			// return new HttpResponse(returnString, HttpURLConnection.HTTP_OK);
			return Response.ok().entity(returnString).build();

		}

		// //////////////////////////////////////////////////////////////////////////////////////
		// Methods required by the LAS2peer framework.
		// //////////////////////////////////////////////////////////////////////////////////////

		/**
		 * Method for debugging purposes. Here the concept of restMapping
		 * validation is shown. It is important to check, if all annotations are
		 * correct and consistent. Otherwise the service will not be accessible
		 * by the WebConnector. Best to do it in the unit tests. To avoid being
		 * overlooked/ignored the method is implemented here and not in the test
		 * section.
		 * 
		 * @return true, if mapping correct
		 */
		/*
		 * public boolean debugMapping() { String XML_LOCATION =
		 * "./restMapping.xml"; String xml = getRESTMapping();
		 * 
		 * try { RESTMapper.writeFile(XML_LOCATION, xml); } catch (IOException
		 * e) { // write error to logfile and console logger.log(Level.SEVERE,
		 * e.toString(), e); // create and publish a monitoring message
		 * L2pLogger.logEvent(this, Event.SERVICE_ERROR, e.toString()); }
		 * 
		 * XMLCheck validator = new XMLCheck(); ValidationResult result =
		 * validator.validate(xml);
		 * 
		 * if (result.isValid()) { return true; } return false; }
		 */

		/**
		 * This method is needed for every RESTful application in LAS2peer.
		 * There is no need to change!
		 * 
		 * @return the mapping
		 */
		/*
		 * public String getRESTMapping() { String result = ""; try { result =
		 * RESTMapper.getMethodsAsXML(this.getClass()); } catch (Exception e) {
		 * // write error to logfile and console logger.log(Level.SEVERE,
		 * e.toString(), e); // create and publish a monitoring message
		 * L2pLogger.logEvent(this, Event.SERVICE_ERROR, e.toString()); } return
		 * result; }
		 */

	}
}
