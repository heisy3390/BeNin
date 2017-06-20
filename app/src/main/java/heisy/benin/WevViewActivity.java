package heisy.benin;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.HttpAuthHandler;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import javax.net.ssl.HttpsURLConnection;

public class WevViewActivity extends AppCompatActivity {

    private ProgressDialog progressDialog;
    private View mProgressView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_wev_view);

//        mProgressView = findViewById(R.id.web_view_progress);

        final Activity activity = this;

        Intent intent = getIntent();
        final String id = intent.getStringExtra("id");
        final String password = intent.getStringExtra("password");
        final String url = intent.getStringExtra("url");

        final WebView webview = new WebView(this);
        setContentView(webview);
        webview.setWebViewClient(new WebViewClient() {
            private boolean mFirstRequest = true;

            @Override
            public void onReceivedHttpAuthRequest(WebView view, HttpAuthHandler handler, String host, String realm) {
                handler.proceed(id, password);
            }

            @Override
            public void onLoadResource(WebView view, String url) {
                // Check to see if there is a progress dialog
//                if (progressDialog == null) {
//                    // If no progress dialog, make one and set message
//                    progressDialog = new ProgressDialog(activity);
//                    progressDialog.setMessage("Loading please wait...");
//                    progressDialog.show();
//
//                    // Hide the webview while loading
//                    webview.setEnabled(false);
//                }
            }

            @Override
            public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
                super.onReceivedHttpError(view, request, errorResponse);
                if (mFirstRequest) {
                    mFirstRequest = false;
                    return;
                }
                if (errorResponse.getStatusCode() == HttpsURLConnection.HTTP_BAD_REQUEST) {
                    return;
                }
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                    progressDialog = null;
                    Toast toast = Toast.makeText(activity, "なんか間違ってるよ！", Toast.LENGTH_LONG);
                    toast.show();

                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(intent);

                    activity.finish();
                }
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                if (progressDialog == null) {
                    // If no progress dialog, make one and set message
                    progressDialog = new ProgressDialog(activity);
                    progressDialog.setMessage("Loading please wait...");
                    progressDialog.show();

                    // Hide the webview while loading
                    webview.setEnabled(false);
                }
            }

            @Override
            public void onPageFinished(WebView view, String url) {
//                mProgressView.setVisibility(View.GONE);
                // Page is done loading;
                // hide the progress dialog and show the webview
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                    progressDialog = null;
                    webview.setEnabled(true);
                }
            }
        });
        webview.loadUrl(url);
    }
}
