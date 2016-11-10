package i5.las2peer.services.versioningService.exception;
import com.google.gson.annotations.Expose;  

/**
 * 
 * Exception thrown when something went wrong during GitHub access.
 *
 */
public class GitHubException extends Exception {

  //private static final long serialVersionUID = -1622464573552868191L;
  @Expose	
  private int responseCode;
  @Expose
  private String responseMessage;
  @Expose
  private String errorStreamString;

  public GitHubException(String message) {
    super(message);
  }
  
  public GitHubException(int _code, String _message, String _error) {
	  responseCode = _code;
	  responseMessage = _message;
	  errorStreamString = _error;
  }
  
  public int getCode() {
	  return responseCode;
  }
  
  public void setCode(int code) {
	  this.responseCode = code; 
	  }
  
  public String getMessage() {
	  return responseMessage;
  }
  
  public void setMessage(String msg) {
	  this.responseMessage = msg; 
  }
  
  public String getErrorStream() {
	  return errorStreamString;
  }
  
  public void setErrorStream(String error) {
	  this.errorStreamString = error; 
	  }
}
