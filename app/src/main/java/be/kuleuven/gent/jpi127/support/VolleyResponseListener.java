package be.kuleuven.gent.jpi127.support;

import com.android.volley.VolleyError;

/**
 * Interface used for the network requests
 *
 * @author Pelle Reyniers
 */
public interface VolleyResponseListener {

    /**
     * Method called when request is started.
     */
    public void requestStarted();

    /**
     * Method called when request is completed without errors.
     * @param response Answer from the server.
     */
    public void requestCompleted(String response);

    /**
     * Method called when request is completed with errors.
     * @param error The generated error
     */
    public void requestEndedWithError(VolleyError error);
}
