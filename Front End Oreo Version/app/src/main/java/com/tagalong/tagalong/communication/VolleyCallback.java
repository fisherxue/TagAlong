package com.tagalong.tagalong.communication;

import org.json.JSONObject;

public interface VolleyCallback {
    void onSuccess (JSONObject response);
    void onError (String result);
}
