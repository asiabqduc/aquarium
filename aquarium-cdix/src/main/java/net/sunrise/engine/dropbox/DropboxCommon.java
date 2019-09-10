/**
 * 
 */
package net.sunrise.engine.dropbox;

import static com.dropbox.core.util.StringUtil.jq;

import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.text.StringEscapeUtils;

import com.dropbox.core.DbxAppInfo;
import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.InvalidAccessTokenException;
import com.dropbox.core.util.LangUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
/**
 * @author bqduc_2
 *
 */
public class DropboxCommon {
  private static final TypeReference<List<DropboxUser>> USER_LIST_TYPE = new TypeReference<List<DropboxUser>>() {};

  public final PrintWriter log;
  public final DbxAppInfo dbxAppInfo;
  public final File userDbFile;
  public final Map<String,DropboxUser> userDb;

  public DropboxCommon(PrintWriter log, DbxAppInfo dbxAppInfo, File userDbFile) throws IOException, DatabaseException {
      this.log = log;
      this.dbxAppInfo = dbxAppInfo;
      this.userDbFile = userDbFile;
      this.userDb = loadUserDb(userDbFile);
  }

  // -------------------------------------------------------------------------------------------
  // User Database
  // -------------------------------------------------------------------------------------------

  private static final ObjectMapper jsonMapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

  private Map<String,DropboxUser> loadUserDb(File dbFile) throws IOException, DatabaseException {
      HashMap<String,DropboxUser> userMap = new HashMap<String,DropboxUser>();

      if (dbFile.isFile()) {
          log.println("Loading User DB from " + jq(dbFile.getPath()) + ".");
          List<DropboxUser> userList = jsonMapper.readValue(dbFile, USER_LIST_TYPE);
          for (DropboxUser user : userList) {
              Object displaced = userMap.put(user.getUsername(), user);
              if (displaced != null) throw new DatabaseException("Couldn't load from database: Duplicate username: " + user.getUsername());
          }
          log.println("Loaded " + userMap.size() + " records.");
      }

      return userMap;
  }

  public void saveUserDb() throws IOException {
      synchronized (this.userDb) {
          jsonMapper.writeValue(this.userDbFile, this.userDb.values());
      }
  }

  public static final class DatabaseException extends Exception {
      private static final long serialVersionUID = 0L;

      public DatabaseException(String message) {
          super(message);
      }
  }

  // -------------------------------------------------------------------------------------------
  // Utility Functions
  // -------------------------------------------------------------------------------------------

  public boolean checkGet(HttpServletRequest request, HttpServletResponse response)
          throws IOException, ServletException {
      if (!request.getMethod().equals("GET")) {
          response.sendError(405);  // 405 - Incorrect method.
          return false;
      }

      return true;
  }

  public boolean checkPost(HttpServletRequest request, HttpServletResponse response)
          throws IOException, ServletException {
      if (!request.getMethod().equals("POST")) {
          page(response, 405, "Incorrect method", "Expecting POST, got " + request.getMethod());
          response.sendError(405);  // 405 - Incorrect method.
          return false;
      }

      String antiCsrfError = FormProtection.checkAntiCsrfToken(request);
      if (antiCsrfError != null) {
          log.println("CSRF error: " + antiCsrfError);
          page(response, 403, "Error", "Forbidden");
          return false;
      }

      return true;
  }

  public DropboxUser getLoggedInUser(HttpServletRequest request) throws IOException {
      String username = (String) request.getSession().getAttribute("logged-in-username");
      if (username == null) {
          return null;
      }
      return userDb.get(username);
  }

  public DropboxUser requireLoggedInUser(HttpServletRequest request, HttpServletResponse response)
          throws IOException {
      DropboxUser user = getLoggedInUser(request);
      if (user == null) {
          pageSoftError(response, "This page requires a logged-in user.  Nobody is logged in.");
          return null;
      }
      return user;
  }

  public DbxRequestConfig getRequestConfig(HttpServletRequest request) {
      return DbxRequestConfig.newBuilder("example-web-file-browser")
          .withUserLocaleFrom(request.getLocale())
          .build();
  }

  public void handleDbxException(HttpServletResponse response, DropboxUser user, DbxException ex, String action)
          throws IOException {
      // The one special error is InvalidAccessToken.  This is probably happening because
      // the user "unlinked" us through Dropbox's website.  Clear their access token in the
      // database (since it's invalid anyway).
      if (ex instanceof InvalidAccessTokenException) {
          page(response, 400, "Cannot access Dropbox account", "It looks like your web-file-browser account is no longer linked to your Dropbox account.");
          user.setAccessToken(null);
          saveUserDb();
          return;
      }

      handleException(response, ex, action);
  }

  public void handleException(HttpServletResponse response, Exception ex, String action) throws IOException {
      // The generic "try again later" is a decent response.
      // - If it's a transient error, then a retry will work.
      // - If it's Dropbox's fault, their service will eventually get fixed and the retry will work (sooner or later).
      // - If it's our fault, we might fix our service and the retry will work eventually.
      page(response, 503, "Error communicating with Dropbox", "Try again later?");

      // But obviously we should still log all these errors because they probably indicate a real problem.
      // - If it's Dropbox's fault, we should record error information so we can notify Dropbox
      //   via their API support e-mail address or the API support forums.
      // - If it's our fault, we obviously want to fix it.
      log.println("Error making Dropbox API call: " + action + ": " + ex.getMessage());
  }

  public void pageSoftError(HttpServletResponse response, String message) throws IOException {
      page(response, 200, "Error", message);
  }

  public void page(HttpServletResponse response, int statusCode, String title, String message) throws IOException {
      title = StringEscapeUtils.escapeHtml4(title);
      message = StringEscapeUtils.escapeHtml4(message);
      response.setContentType("text/html");
      response.setCharacterEncoding("utf-8");
      PrintWriter out = new PrintWriter(new OutputStreamWriter(response.getOutputStream(), "UTF-8"));
      out.println("<html>");
      out.println("<head><title>" + title + "</title></head>");
      out.println("<body>");
      out.println("<h2>" + title + "</h2>");
      out.println("<p>" + message + "</p>");
      out.println("</body>");
      out.println("</html>");
      out.flush();
      response.setStatus(statusCode);
  }

  public String getUrl(HttpServletRequest request, String path) {
      URL requestUrl;
      try {
          requestUrl = new URL(request.getRequestURL().toString());
          return new URL(requestUrl, path).toExternalForm();
      } catch (MalformedURLException ex) {
          throw LangUtil.mkAssert("Bad URL", ex);
      }
  }

}
