/*
 * Copyright (C) 2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.internal.widget;

import com.android.internal.R;

import android.Manifest;
import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.content.res.Resources.NotFoundException;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.SystemClock;
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.Data;
import android.provider.ContactsContract.PhoneLookup;
import android.provider.ContactsContract.RawContacts;
import android.provider.ContactsContract.StatusUpdates;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.provider.ContactsContract.CommonDataKinds.Photo;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.QuickContactBadge;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Header used across system for displaying a title bar with contact info. You
 * can bind specific values on the header, or use helper methods like
 * {@link #bindFromContactId(long)} to populate asynchronously.
 * <p>
 * The parent must request the {@link Manifest.permission#READ_CONTACTS}
 * permission to access contact data.
 */
public class ContactHeaderWidget extends FrameLayout implements View.OnClickListener {

    private static final String TAG = "ContactHeaderWidget";

    private TextView mDisplayNameView;
    private View mAggregateBadge;
    private TextView mPhoneticNameView;
    private CheckBox mStarredView;
    private QuickContactBadge mPhotoView;
    private ImageView mPresenceView;
    private TextView mStatusView;
    private TextView mStatusAttributionView;
    private int mNoPhotoResource;
    private QueryHandler mQueryHandler;

    protected Uri mContactUri;

    protected String[] mExcludeMimes = null;

    protected ContentResolver mContentResolver;

    /**
     * Interface for callbacks invoked when the user interacts with a header.
     */
    public interface ContactHeaderListener {
        public void onPhotoClick(View view);
        public void onDisplayNameClick(View view);
    }

    private ContactHeaderListener mListener;


    private interface ContactQuery {
        //Projection used for the summary info in the header.
        String[] COLUMNS = new String[] {
            Contacts._ID,
            Contacts.LOOKUP_KEY,
            Contacts.PHOTO_ID,
            Contacts.DISPLAY_NAME,
            Contacts.STARRED,
            Contacts.CONTACT_PRESENCE,
            Contacts.CONTACT_STATUS,
            Contacts.CONTACT_STATUS_TIMESTAMP,
            Contacts.CONTACT_STATUS_RES_PACKAGE,
            Contacts.CONTACT_STATUS_LABEL,
        };
        int _ID = 0;
        int LOOKUP_KEY = 1;
        int PHOTO_ID = 2;
        int DISPLAY_NAME = 3;
        //TODO: We need to figure out how we're going to get the phonetic name.
        //static final int HEADER_PHONETIC_NAME_COLUMN_INDEX
        int STARRED = 4;
        int CONTACT_PRESENCE_STATUS = 5;
        int CONTACT_STATUS = 6;
        int CONTACT_STATUS_TIMESTAMP = 7;
        int CONTACT_STATUS_RES_PACKAGE = 8;
        int CONTACT_STATUS_LABEL = 9;
    }

    //Projection used for looking up contact id from phone number
    protected static final String[] PHONE_LOOKUP_PROJECTION = new String[] {
        PhoneLookup._ID,
        PhoneLookup.LOOKUP_KEY,
    };
    protected static final int PHONE_LOOKUP_CONTACT_ID_COLUMN_INDEX = 0;
    protected static final int PHONE_LOOKUP_CONTACT_LOOKUP_KEY_COLUMN_INDEX = 1;

    //Projection used for looking up contact id from email address
    protected static final String[] EMAIL_LOOKUP_PROJECTION = new String[] {
        RawContacts.CONTACT_ID,
        Contacts.LOOKUP_KEY,
    };
    protected static final int EMAIL_LOOKUP_CONTACT_ID_COLUMN_INDEX = 0;
    protected static final int EMAIL_LOOKUP_CONTACT_LOOKUP_KEY_COLUMN_INDEX = 1;

    protected static final String[] CONTACT_LOOKUP_PROJECTION = new String[] {
        Contacts._ID,
    };
    protected static final int CONTACT_LOOKUP_ID_COLUMN_INDEX = 0;

    private static final int TOKEN_CONTACT_INFO = 0;
    private static final int TOKEN_PHONE_LOOKUP = 1;
    private static final int TOKEN_EMAIL_LOOKUP = 2;

    public ContactHeaderWidget(Context context) {
        this(context, null);
    }

    public ContactHeaderWidget(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ContactHeaderWidget(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        mContentResolver = mContext.getContentResolver();

        LayoutInflater inflater =
            (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.contact_header, this);

        mDisplayNameView = (TextView) findViewById(R.id.name);
        mAggregateBadge = findViewById(R.id.aggregate_badge);
        mAggregateBadge.setVisibility(View.GONE);

        mPhoneticNameView = (TextView) findViewById(R.id.phonetic_name);

        mStarredView = (CheckBox)findViewById(R.id.star);
        mStarredView.setOnClickListener(this);

        mPhotoView = (QuickContactBadge) findViewById(R.id.photo);

        mPresenceView = (ImageView) findViewById(R.id.presence);

        mStatusView = (TextView)findViewById(R.id.status);
        mStatusAttributionView = (TextView)findViewById(R.id.status_date);

        // Set the photo with a random "no contact" image
        long now = SystemClock.elapsedRealtime();
        int num = (int) now & 0xf;
        if (num < 9) {
            // Leaning in from right, common
            mNoPhotoResource = R.drawable.ic_contact_picture;
        } else if (num < 14) {
            // Leaning in from left uncommon
            mNoPhotoResource = R.drawable.ic_contact_picture_2;
        } else {
            // Coming in from the top, rare
            mNoPhotoResource = R.drawable.ic_contact_picture_3;
        }

        mQueryHandler = new QueryHandler(mContentResolver);
    }

    public void enableClickListeners() {
        mDisplayNameView.setOnClickListener(this);
        mPhotoView.setOnClickListener(this);
    }

    /**
     * Set the given {@link ContactHeaderListener} to handle header events.
     */
    public void setContactHeaderListener(ContactHeaderListener listener) {
        mListener = listener;
    }

    private void performPhotoClick() {
        if (mListener != null) {
            mListener.onPhotoClick(mPhotoView);
        }
    }

    private void performDisplayNameClick() {
        if (mListener != null) {
            mListener.onDisplayNameClick(mDisplayNameView);
        }
    }

    private class QueryHandler extends AsyncQueryHandler {

        public QueryHandler(ContentResolver cr) {
            super(cr);
        }

        @Override
        protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
            try{
                switch (token) {
                    case TOKEN_CONTACT_INFO: {
                        bindContactInfo(cursor);
                        invalidate();
                        break;
                    }
                    case TOKEN_PHONE_LOOKUP: {
                        if (cursor != null && cursor.moveToFirst()) {
                            long contactId = cursor.getLong(PHONE_LOOKUP_CONTACT_ID_COLUMN_INDEX);
                            String lookupKey = cursor.getString(
                                    PHONE_LOOKUP_CONTACT_LOOKUP_KEY_COLUMN_INDEX);
                            bindFromContactUri(Contacts.getLookupUri(contactId, lookupKey));
                        } else {
                            String phoneNumber = (String) cookie;
                            setDisplayName(phoneNumber, null);
                            mPhotoView.assignContactFromPhone(phoneNumber, true);
                        }
                        break;
                    }
                    case TOKEN_EMAIL_LOOKUP: {
                        if (cursor != null && cursor.moveToFirst()) {
                            long contactId = cursor.getLong(EMAIL_LOOKUP_CONTACT_ID_COLUMN_INDEX);
                            String lookupKey = cursor.getString(
                                    EMAIL_LOOKUP_CONTACT_LOOKUP_KEY_COLUMN_INDEX);
                            bindFromContactUri(Contacts.getLookupUri(contactId, lookupKey));
                        } else {
                            String emailAddress = (String) cookie;
                            setDisplayName(emailAddress, null);
                            mPhotoView.assignContactFromEmail(emailAddress, true);
                        }
                        break;
                    }
                }
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        }
    }

    /**
     * Turn on/off showing of the aggregate bage element.
     */
    public void showAggregateBadge(boolean showBagde) {
        mAggregateBadge.setVisibility(showBagde ? View.VISIBLE : View.GONE);
    }

    /**
     * Turn on/off showing of the star element.
     */
    public void showStar(boolean showStar) {
        mStarredView.setVisibility(showStar ? View.VISIBLE : View.GONE);
    }

    /**
     * Manually set the starred state of this header widget. This doesn't change
     * the underlying {@link Contacts} value, only the UI state.
     */
    public void setStared(boolean starred) {
        mStarredView.setChecked(starred);
    }

    /**
     * Manually set the presence.
     */
    public void setPresence(int presence) {
        mPresenceView.setImageResource(StatusUpdates.getPresenceIconResourceId(presence));
    }

    /**
     * Manually set the contact uri
     */
    public void setContactUri(Uri uri) {
        setContactUri(uri, true);
    }

    /**
     * Manually set the contact uri
     */
    public void setContactUri(Uri uri, boolean sendToFastrack) {
        mContactUri = uri;
        if (sendToFastrack) {
            mPhotoView.assignContactUri(uri);
        }
    }

    /**
     * Manually set the photo to display in the header. This doesn't change the
     * underlying {@link Contacts}, only the UI state.
     */
    public void setPhoto(Bitmap bitmap) {
        mPhotoView.setImageBitmap(bitmap);
    }

    /**
     * Manually set the display name and phonetic name to show in the header.
     * This doesn't change the underlying {@link Contacts}, only the UI state.
     */
    public void setDisplayName(CharSequence displayName, CharSequence phoneticName) {
        mDisplayNameView.setText(displayName);
        if (mPhoneticNameView != null) {
            mPhoneticNameView.setText(phoneticName);
        }
    }

    /**
     * Manually set the social snippet text to display in the header.
     */
    public void setSocialSnippet(CharSequence snippet) {
        if (snippet == null) {
            mStatusView.setVisibility(View.GONE);
        } else {
            mStatusView.setText(snippet);
            mStatusView.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Set a list of specific MIME-types to exclude and not display. For
     * example, this can be used to hide the {@link Contacts#CONTENT_ITEM_TYPE}
     * profile icon.
     */
    public void setExcludeMimes(String[] excludeMimes) {
        mExcludeMimes = excludeMimes;
        mPhotoView.setExcludeMimes(excludeMimes);
    }

    /**
     * Convenience method for binding all available data from an existing
     * contact.
     *
     * @param conatctUri a {Contacts.CONTENT_LOOKUP_URI} style URI.
     */
    public void bindFromContactLookupUri(Uri contactLookupUri) {
        mContactUri = contactLookupUri;
        startContactQuery(contactLookupUri);
    }

    /**
     * Convenience method for binding all available data from an existing
     * contact.
     *
     * @param conatctUri a {Contacts.CONTENT_URI} style URI.
     */
    public void bindFromContactUri(Uri contactUri) {
        mContactUri = contactUri;
        long contactId = ContentUris.parseId(contactUri);

        startContactQuery(contactUri);
    }

    /**
     * Convenience method for binding all available data from an existing
     * contact.
     *
     * @param emailAddress The email address used to do a reverse lookup in
     * the contacts database. If more than one contact contains this email
     * address, one of them will be chosen to bind to.
     */
    public void bindFromEmail(String emailAddress) {
        mQueryHandler.startQuery(TOKEN_EMAIL_LOOKUP, emailAddress,
                Uri.withAppendedPath(Email.CONTENT_LOOKUP_URI, Uri.encode(emailAddress)),
                EMAIL_LOOKUP_PROJECTION, null, null, null);
    }

    /**
     * Convenience method for binding all available data from an existing
     * contact.
     *
     * @param number The phone number used to do a reverse lookup in
     * the contacts database. If more than one contact contains this phone
     * number, one of them will be chosen to bind to.
     */
    public void bindFromPhoneNumber(String number) {
        mQueryHandler.startQuery(TOKEN_PHONE_LOOKUP, number,
                Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number)),
                PHONE_LOOKUP_PROJECTION, null, null, null);
    }

    /**
     * Method to force this widget to forget everything it knows about the contact.
     * The widget isn't automatically updated or redrawn.
     *
     */
    public void wipeClean() {
        setPhoto(null);
        mContactUri = null;
        mExcludeMimes = null;
    }

    private void startContactQuery(Uri contactUri) {
        mQueryHandler.startQuery(TOKEN_CONTACT_INFO, null, contactUri, ContactQuery.COLUMNS,
                null, null, null);
    }

    /**
     * Bind the contact details provided by the given {@link Cursor}.
     */
    protected void bindContactInfo(Cursor c) {
        if (c == null || !c.moveToFirst()) return;

        // TODO: Bring back phonetic name
        final String displayName = c.getString(ContactQuery.DISPLAY_NAME);
        final long contactId = c.getLong(ContactQuery._ID);
        final String lookupKey = c.getString(ContactQuery.LOOKUP_KEY);
        final String phoneticName = null;
        this.setDisplayName(displayName, null);

        final boolean starred = c.getInt(ContactQuery.STARRED) != 0;
        mStarredView.setChecked(starred);

        //Set the photo
        Bitmap photoBitmap = loadContactPhoto(c.getLong(ContactQuery.PHOTO_ID), null);
        if (photoBitmap == null) {
            photoBitmap = loadPlaceholderPhoto(null);
        }
        mPhotoView.setImageBitmap(photoBitmap);
        mPhotoView.assignContactUri(Contacts.getLookupUri(contactId, lookupKey));

        //Set the presence status
        if (!c.isNull(ContactQuery.CONTACT_PRESENCE_STATUS)) {
            int presence = c.getInt(ContactQuery.CONTACT_PRESENCE_STATUS);
            mPresenceView.setImageResource(StatusUpdates.getPresenceIconResourceId(presence));
            mPresenceView.setVisibility(View.VISIBLE);
        } else {
            mPresenceView.setVisibility(View.GONE);
        }

        //Set the status update
        String status = c.getString(ContactQuery.CONTACT_STATUS);
        if (!TextUtils.isEmpty(status)) {
            mStatusView.setText(status);
            mStatusView.setVisibility(View.VISIBLE);

            CharSequence timestamp = null;

            if (!c.isNull(ContactQuery.CONTACT_STATUS_TIMESTAMP)) {
                long date = c.getLong(ContactQuery.CONTACT_STATUS_TIMESTAMP);

                // Set the date/time field by mixing relative and absolute
                // times.
                int flags = DateUtils.FORMAT_ABBREV_RELATIVE;

                timestamp = DateUtils.getRelativeTimeSpanString(date,
                        System.currentTimeMillis(), DateUtils.MINUTE_IN_MILLIS, flags);
            }

            String label = null;

            if (!c.isNull(ContactQuery.CONTACT_STATUS_LABEL)) {
                String resPackage = c.getString(ContactQuery.CONTACT_STATUS_RES_PACKAGE);
                int labelResource = c.getInt(ContactQuery.CONTACT_STATUS_LABEL);
                Resources resources;
                if (TextUtils.isEmpty(resPackage)) {
                    resources = getResources();
                } else {
                    PackageManager pm = getContext().getPackageManager();
                    try {
                        resources = pm.getResourcesForApplication(resPackage);
                    } catch (NameNotFoundException e) {
                        Log.w(TAG, "Contact status update resource package not found: "
                                + resPackage);
                        resources = null;
                    }
                }

                if (resources != null) {
                    try {
                        label = resources.getString(labelResource);
                    } catch (NotFoundException e) {
                        Log.w(TAG, "Contact status update resource not found: " + resPackage + "@"
                                + labelResource);
                    }
                }
            }

            CharSequence attribution;
            if (timestamp != null && label != null) {
                attribution = getContext().getString(
                        R.string.contact_status_update_attribution_with_date,
                        timestamp, label);
            } else if (timestamp == null && label != null) {
                attribution = getContext().getString(
                        R.string.contact_status_update_attribution,
                        label);
            } else if (timestamp != null) {
                attribution = timestamp;
            } else {
                attribution = null;
            }
            if (attribution != null) {
                mStatusAttributionView.setText(attribution);
                mStatusAttributionView.setVisibility(View.VISIBLE);
            } else {
                mStatusAttributionView.setVisibility(View.GONE);
            }
        } else {
            mStatusView.setVisibility(View.GONE);
            mStatusAttributionView.setVisibility(View.GONE);
        }
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.star: {
                // Toggle "starred" state
                // Make sure there is a contact
                if (mContactUri != null) {
                    final ContentValues values = new ContentValues(1);
                    values.put(Contacts.STARRED, mStarredView.isChecked());
                    mContentResolver.update(mContactUri, values, null, null);
                }
                break;
            }
            case R.id.photo: {
                performPhotoClick();
                break;
            }
            case R.id.name: {
                performDisplayNameClick();
                break;
            }
        }
    }

    private Bitmap loadContactPhoto(long photoId, BitmapFactory.Options options) {
        Cursor photoCursor = null;
        Bitmap photoBm = null;

        try {
            photoCursor = mContentResolver.query(
                    ContentUris.withAppendedId(Data.CONTENT_URI, photoId),
                    new String[] { Photo.PHOTO },
                    null, null, null);

            if (photoCursor != null && photoCursor.moveToFirst() && !photoCursor.isNull(0)) {
                byte[] photoData = photoCursor.getBlob(0);
                photoBm = BitmapFactory.decodeByteArray(photoData, 0,
                        photoData.length, options);
            }
        } finally {
            if (photoCursor != null) {
                photoCursor.close();
            }
        }

        return photoBm;
    }

    private Bitmap loadPlaceholderPhoto(BitmapFactory.Options options) {
        if (mNoPhotoResource == 0) {
            return null;
        }
        return BitmapFactory.decodeResource(mContext.getResources(),
                mNoPhotoResource, options);
    }
}
