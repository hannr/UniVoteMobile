package ch.bfh.univote.registration;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.net.http.SslError;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * This component is able to handle unicert urls and ssl connections.
 * @author Raphael Haenni
 */
public class UnicertWebViewClient extends WebViewClient {
	private Context context;

	/**
	 * Creates new UnicertWebViewClient.
	 * @param context The context.
	 */
	public UnicertWebViewClient(Context context) {
		this.context = context;
	}
		
	public void onReceivedSslError (WebView view, SslErrorHandler handler, SslError error) {
		 handler.proceed() ;
	}
        
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        if(url.startsWith("unicert://")) {
        	Uri myUrl = Uri.parse(url);
        	Intent intent = new Intent(this.context, RegistrationActivity.class);
        	intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        	intent.putExtra("JSESSIONID", myUrl.getQueryParameter("JSESSIONID"));
        	intent.putExtra("idp", myUrl.getQueryParameter("idp"));
        	intent.putExtra("mail", myUrl.getQueryParameter("mail"));
        	intent.putExtra("id", myUrl.getQueryParameter("id"));
        	this.context.startActivity(intent);
        	return true;
    	}
        return super.shouldOverrideUrlLoading(view, url);
    }    
}