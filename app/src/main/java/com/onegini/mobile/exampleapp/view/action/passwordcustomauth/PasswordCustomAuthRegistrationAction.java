/*
 * Copyright (c) 2016-2017 Onegini B.V.
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

package com.onegini.mobile.exampleapp.view.action.passwordcustomauth;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

import android.content.Context;
import android.content.Intent;
import com.onegini.mobile.exampleapp.view.activity.PasswordAuthenticatorRegistrationActivity;
import com.onegini.mobile.sdk.android.handlers.customauth.OneginiCustomAuthRegistrationAction;
import com.onegini.mobile.sdk.android.handlers.request.callback.OneginiCustomAuthRegistrationCallback;

public class PasswordCustomAuthRegistrationAction implements OneginiCustomAuthRegistrationAction {

  public static OneginiCustomAuthRegistrationCallback CALLBACK;

  private Context context;

  public PasswordCustomAuthRegistrationAction(final Context context) {
    this.context = context;
  }

  @Override
  public void finishRegistration(final OneginiCustomAuthRegistrationCallback callback) {
    CALLBACK = callback;

    final Intent intent = new Intent(context, PasswordAuthenticatorRegistrationActivity.class);
    intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
    context.startActivity(intent);
  }
}
