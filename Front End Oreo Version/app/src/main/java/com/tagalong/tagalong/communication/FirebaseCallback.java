package com.tagalong.tagalong.communication;

import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.InstanceIdResult;
import androidx.annotation.NonNull;

public interface FirebaseCallback {
    void onSuccess (@NonNull Task<InstanceIdResult> task);
}
