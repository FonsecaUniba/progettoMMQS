package zame.promo;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.HttpAuthHandler;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.Toast;
import java.io.InputStream;
import java.util.Locale;
import zame.game.BuildConfig;

/**
 * Class Representing the Ad View
 */
public class PromoView extends FrameLayout {
    /**
     * Ad URL
     */
    protected static final String PROMO_URL = "http://mobile.zame-dev.org/promo/index.php?package=";

    /**
     * Reload Interval
     */
    protected static final long RELOAD_INTERVAL = 10L * 1000L;
    /**
     * Rotation Interval
     */
    protected static final long ROTATE_INTERVAL = 15L * 1000L;

    /**
     * Constant for Initialization
     */
    protected static final int STATE_INITIALIZED = 0;
    /**
     * Constant for Loading
     */
    protected static final int STATE_LOADING = 1;
    /**
     * Constant for Loaded
     */
    protected static final int STATE_LOADED = 2;
    /**
     * Constant for Dismissed
     */
    protected static final int STATE_DISMISSED = 3;

    /**
     * Event Handler
     */
    protected final Handler handler = new Handler();
    /**
     * View Context
     */
    protected Context context;
    /**
     * Previous Web View
     */
    protected WebView prevWebView;
    /**
     * Current Web View
     */
    protected WebView currentWebView;
    /**
     * View state
     */
    protected int state;

    /**
     * Loads the Ad
     */
    protected Runnable loadPromoRunnable = new Runnable() {
        @Override
        public void run() {
            loadPromo();
        }
    };

    /**
     * Reloads the Ad
     */
    protected Runnable reloadPromoRunnable = new Runnable() {
        @Override
        public void run() {
            reloadPromo();
        }
    };

    /**
     * Rotates the Ad
     */
    protected Runnable rotatePromoRunnable = new Runnable() {
        @Override
        public void run() {
            rotatePromo();
        }
    };

    /**
     * When Ad is Loaded
     */
    protected Runnable promoLoadedRunnable = new Runnable() {
        @Override
        public void run() {
            promoLoaded();
        }
    };

    /**
     * Dismiss Ad
     */
    protected Runnable promoDismissedRunnable = new Runnable() {
        @Override
        public void run() {
            promoDismissed();
        }
    };

    /**
     * Initializes Ad
     */
    private void init(Context c)
    {
        initialize(c);
    }

    /**
     * Class Constructor
     * @param context App Context
     */
    public PromoView(Context context) {
        super(context);
        init(context);
    }

    /**
     * Class Constructor
     * @param context App Context
     * @param attrs App Attributes
     */
    public PromoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    /**
     * Class Constructor
     * @param context App Context
     * @param attrs App Attributes
     * @param defStyle View Style
     */
    public PromoView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    /**
     * Initializes the Class
     * @param cont App Context
     */
    protected void initialize(Context cont) {
        this.context = cont;

        prevWebView = createWebView();
        currentWebView = createWebView();

        loadPromo();
    }

    /**
     * Creates a WebView
     * @return Nothing because it's disabled
     */
    @SuppressLint({ "AddJavascriptInterface", "SetJavaScriptEnabled" })
    protected WebView createWebView() {
        WebView webView = new WebView(context);
        //webView.addJavascriptInterface(new JsApi(), "promoApi");
        webView.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);
        webView.setScrollbarFadingEnabled(true);
        webView.setWebViewClient(new PromoWebViewClient());
        webView.setWebChromeClient(new PromoWebChromeClient());
        webView.setVisibility(View.INVISIBLE);

        webView.setBackgroundColor(0);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            webView.setLayerType(WebView.LAYER_TYPE_SOFTWARE, null);
        }

        webView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return true;
            }
        });

        WebSettings webSettings = webView.getSettings();
        webSettings.setBuiltInZoomControls(false);
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        webSettings.setJavaScriptEnabled(false);
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);
        webSettings.setSupportZoom(false);
        webSettings.setUseWideViewPort(true);
        webSettings.setSupportMultipleWindows(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            webSettings.setDisplayZoomControls(false);
        }

        webView.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));

        addView(webView);

        return webView;
    }

    /**
     * Loads the Ad
     */
    protected void loadPromo() {
        handler.removeCallbacks(loadPromoRunnable);
        handler.removeCallbacks(reloadPromoRunnable);
        handler.removeCallbacks(rotatePromoRunnable);

        if (state != STATE_INITIALIZED) {
            return;
        }

        if (isNetworkConnected()) {
            state = STATE_LOADING;

            String url = PROMO_URL + context.getPackageName() + "&lang=" + Locale.getDefault()
                    .getLanguage()
                    .toLowerCase(Locale.US);

            if (BuildConfig.DEBUG) {
                currentWebView.loadUrl(url + "&mode=debug");
            } else {
                currentWebView.loadUrl(url);
            }
        } else {
            handler.postDelayed(loadPromoRunnable, RELOAD_INTERVAL);
        }
    }

    /**
     * Reloads the Ad
     */
    protected void reloadPromo() {
        currentWebView.setVisibility(View.INVISIBLE);
        currentWebView.stopLoading();
        currentWebView.loadData("", "text/html", null);

        state = STATE_INITIALIZED;
        loadPromo();
    }

    /**
     * Rotates the Ad
     */
    protected void rotatePromo() {
        WebView tmpWebView = prevWebView;
        prevWebView = currentWebView;
        currentWebView = tmpWebView;

        reloadPromo();
    }

    /**
     * Loads the Ad
     */
    protected void promoLoaded() {
        if (state == STATE_LOADING) {
            currentWebView.setVisibility(View.VISIBLE);

            prevWebView.setVisibility(View.INVISIBLE);
            prevWebView.stopLoading();
            prevWebView.loadData("", "text/html", null);

            state = STATE_LOADED;
            handler.postDelayed(rotatePromoRunnable, ROTATE_INTERVAL);
        }
    }

    /**
     * Dismiss the Ad
     */
    protected void promoDismissed() {
        if ((state == STATE_LOADING) || (state == STATE_LOADED)) {
            prevWebView.setVisibility(View.INVISIBLE);
            prevWebView.stopLoading();
            prevWebView.loadData("", "text/html", null);

            currentWebView.setVisibility(View.INVISIBLE);
            currentWebView.stopLoading();
            currentWebView.loadData("", "text/html", null);

            state = STATE_DISMISSED;
            handler.postDelayed(rotatePromoRunnable, ROTATE_INTERVAL);
        }
    }

    /**
     * When View Focus Changes
     * @param hasWindowFocus Does View have focus?
     */
    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);

        if (state != STATE_INITIALIZED) {
            return;
        }

        if (hasWindowFocus) {
            loadPromo();
        } else {
            handler.removeCallbacks(loadPromoRunnable);
        }
    }

    /**
     * When View is attached
     */
    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        if (state == STATE_INITIALIZED) {
            loadPromo();
        }
    }

    /**
     * When View is Detached
     */
    @Override
    protected void onDetachedFromWindow() {
        handler.removeCallbacks(loadPromoRunnable);
        handler.removeCallbacks(reloadPromoRunnable);
        handler.removeCallbacks(rotatePromoRunnable);
        state = STATE_INITIALIZED;

        super.onDetachedFromWindow();
    }

    /**
     * Is network connected?
     * @return true if connected, false otherwise
     */
    protected boolean isNetworkConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager == null) {
            return false;
        }

        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return ((networkInfo != null) && networkInfo.isConnected());
    }

    /**
     * Opens External Browser
     * @param uri URI to open
     */
    protected void openExternalBrowser(final String uri) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    context.startActivity((new Intent(Intent.ACTION_VIEW,
                            Uri.parse(uri))).addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET));
                } catch (Exception ex) {
                    try {
                        Toast.makeText(context, "Could not launch the browser application.", Toast.LENGTH_LONG).show();
                    } catch (Exception inner) {
                        System.err.println();
                    }
                }
            }
        });
    }

    /**
     * Opens External Intent
     * @param intent Intent to Open
     */
    protected void openExternalIntent(final Intent intent) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    context.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET));
                } catch (Exception ex) {
                    try {
                        Toast.makeText(context, "Could not start external intent.", Toast.LENGTH_LONG).show();
                    } catch (Exception inner) {
                        System.err.println();
                    }
                }
            }
        });
    }

    /**
     * Class representing Javascript API
     */
    @SuppressWarnings("unused")
    protected class JsApi {
        /**
         * When Javascript is loaded
         */
        @JavascriptInterface
        public void loaded() {
            //noinspection MagicNumber
            handler.postDelayed(promoLoadedRunnable, 100L);
        }

        /**
         * When Javascript is dismissed
         */
        @JavascriptInterface
        public void dismiss() {
            handler.post(promoDismissedRunnable);
        }
    }

    /**
     * Class representing the WebViewClient
     */
    private class PromoWebViewClient extends WebViewClient {
        /**
         * When Page Finishes loading
         * @param view WebView
         * @param url URL to load
         */
        @Override
        public void onPageFinished(WebView view, String url) {
            view.setBackgroundColor(0);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                view.setLayerType(WebView.LAYER_TYPE_SOFTWARE, null);
            }
        }

        /**
         * Should we Override URL loading?
         * @param view WebView
         * @param url URL to load
         * @return True or false
         */
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            final String MAILTO_PREFIX = "mailto:";

            if (url.startsWith(MAILTO_PREFIX)) {
                Intent intent = new Intent(Intent.ACTION_SENDTO,
                        Uri.fromParts("mailto", url.replaceFirst(MAILTO_PREFIX, ""), null));

                openExternalIntent(intent);
                return true;
            }

            return false;
        }

        /**
         * Should we Intercept Request?
         * @param view WebView
         * @param url URL to load
         * @return True or false
         */
        @SuppressLint("NewApi")
        @Override
        public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
            final String ANDROID_ASSET = "file:///android_asset/";

            if (url.startsWith(ANDROID_ASSET)) {
                try {
                    Uri uri = Uri.parse(url.replaceFirst(ANDROID_ASSET, ""));
                    InputStream stream = view.getContext().getAssets().open(uri.getPath());
                    return new WebResourceResponse("text/html", "UTF-8", stream);
                } catch (Exception ex) {
                    System.err.println();
                }
            }

            return null;
        }

        /**
         * When Error is received
         * @param view WebView
         * @param errorCode Error Code
         * @param description Error Description
         * @param failingUrl URL which failed
         */
        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            view.stopLoading();
            view.loadData("", "text/html", null);

            handler.post(reloadPromoRunnable);
        }

        /**
         * When HTTP Auth Request is received
         * @param view WebView
         * @param httpAuthHandler HTTPAuth Handler
         * @param host Host who requested Auth
         * @param realm Realm
         */
        @Override
        public void onReceivedHttpAuthRequest(WebView view,
                HttpAuthHandler httpAuthHandler,
                String host,
                String realm) {

            view.stopLoading();
            view.loadData("", "text/html", null);

            handler.post(reloadPromoRunnable);
        }
    }

    /**
     * Class representing Chrome Client
     */
    private class PromoWebChromeClient extends WebChromeClient {
        private WebView childWebView;

        /**
         * When View is created
         * @param view View
         * @param dialog Is dialog?
         * @param userGesture Does it support UserGestures?
         * @param resultMsg Message to show
         * @return true if created, false otherwise
         */
        @Override
        public boolean onCreateWindow(WebView view, boolean dialog, boolean userGesture, Message resultMsg) {
            try {
                if (childWebView != null) {
                    childWebView.stopLoading();
                    childWebView.destroy();
                }

                childWebView = new WebView(view.getContext());

                childWebView.setWebViewClient(new WebViewClient() {
                    @Override
                    public boolean shouldOverrideUrlLoading(WebView view, String url) {
                        url = url.trim();

                        //noinspection SizeReplaceableByIsEmpty
                        if (url.length() != 0) {
                            openExternalBrowser(url);
                        }

                        childWebView.stopLoading();
                        childWebView.destroy();
                        childWebView = null;

                        return true;
                    }
                });

                ((WebView.WebViewTransport)resultMsg.obj).setWebView(childWebView);
                resultMsg.sendToTarget();

                return true;
            } catch (Exception ex) {
                return false;
            }
        }
    }
}
