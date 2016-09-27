/*
 * Copyright (c) 2016 Onegini B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.onegini.mobile.exampleapp.network.gcm;

import java.util.Set;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import com.onegini.mobile.exampleapp.OneginiSDK;
import com.onegini.mobile.exampleapp.storage.SettingsStorage;
import com.onegini.mobile.exampleapp.storage.UserStorage;
import com.onegini.mobile.sdk.android.client.OneginiClient;
import com.onegini.mobile.sdk.android.exception.OneginiInitializationException;
import com.onegini.mobile.sdk.android.handlers.OneginiInitializationHandler;
import com.onegini.mobile.sdk.android.handlers.OneginiMobileAuthenticationHandler;
import com.onegini.mobile.sdk.android.handlers.error.OneginiInitializationError;
import com.onegini.mobile.sdk.android.handlers.error.OneginiMobileAuthenticationError;
import com.onegini.mobile.sdk.android.model.entity.UserProfile;

public class GCMIntentService extends IntentService {

  private static final String TAG = GCMIntentService.class.getSimpleName();

  public GCMIntentService() {
    super(TAG);
  }

  @Override
  protected void onHandleIntent(final Intent intent) {
    final Bundle extras = intent.getExtras();
    if (!extras.isEmpty()) {
      Log.i(TAG, "Push message received");

      try {
        handleMobileAuthenticationRequest(extras);
      } catch (OneginiInitializationException exception) {
        // Onegini SDK hasn't been started yet so we have to do it
        // before handling the mobile authentication request
        setupOneginiSDK(extras);
      }
    }
    // Release the wake lock provided by the WakefulBroadcastReceiver.
    GCMBroadcastReceiver.completeWakefulIntent(intent);
  }

  private void setupOneginiSDK(final Bundle extras) {
    final OneginiClient oneginiClient = OneginiSDK.getOneginiClient(this);
    oneginiClient.start(new OneginiInitializationHandler() {
      @Override
      public void onSuccess(final Set<UserProfile> removedUserProfiles) {
        if (removedUserProfiles.isEmpty()) {
          handleMobileAuthenticationRequest(extras);
        } else {
          removeUserProfiles(removedUserProfiles, extras);
        }
      }

      @Override
      public void onError(final OneginiInitializationError error) {
        Toast.makeText(GCMIntentService.this, error.getErrorDescription(), Toast.LENGTH_LONG).show();
      }
    });
  }

  private void handleMobileAuthenticationRequest(final Bundle extras) {
    OneginiSDK.getOneginiClient(this).getUserClient().handleMobileAuthenticationRequest(extras, new OneginiMobileAuthenticationHandler() {
      @Override
      public void onSuccess() {
        Toast.makeText(GCMIntentService.this, "Mobile authentication success", Toast.LENGTH_SHORT).show();
      }

      @Override
      public void onError(final OneginiMobileAuthenticationError oneginiMobileAuthenticationError) {
        Toast.makeText(GCMIntentService.this, oneginiMobileAuthenticationError.getErrorDescription(), Toast.LENGTH_SHORT).show();
        if (oneginiMobileAuthenticationError.getErrorType() == OneginiMobileAuthenticationError.USER_DEREGISTERED) {
          new SettingsStorage(GCMIntentService.this).setMobileAuthenticationEnabled(false);
        } else if (oneginiMobileAuthenticationError.getErrorType() == OneginiMobileAuthenticationError.ACTION_CANCELED) {
          // the user denied incoming mobile authentication request
        }
      }
    });
  }

  private void removeUserProfiles(final Set<UserProfile> removedUserProfiles, final Bundle extras) {
    final UserStorage userStorage = new UserStorage(this);
    for (final UserProfile userProfile : removedUserProfiles) {
      userStorage.removeUser(userProfile);
    }
    handleMobileAuthenticationRequest(extras);
  }
}