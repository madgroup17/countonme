package it.polito.mad.countonme;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import butterknife.Unbinder;
import it.polito.mad.countonme.UI.DatePicker;
import it.polito.mad.countonme.UI.ErrorDialog;
import it.polito.mad.countonme.UI.ImageSourceDialog;
import it.polito.mad.countonme.customviews.RequiredInputTextView;
import it.polito.mad.countonme.database.DataManager;
import it.polito.mad.countonme.database.ExpenseLoader;
import it.polito.mad.countonme.database.SharingActivityLoader;
import it.polito.mad.countonme.exceptions.DataLoaderException;
import it.polito.mad.countonme.exceptions.InvalidDataException;
import it.polito.mad.countonme.interfaces.IOnDataListener;
import it.polito.mad.countonme.lists.UsersAdapter;
import it.polito.mad.countonme.models.Expense;
import it.polito.mad.countonme.models.Share;
import it.polito.mad.countonme.models.SharingActivity;
import it.polito.mad.countonme.models.User;
import it.polito.mad.countonme.storage.StorageManager;

import static android.app.Activity.RESULT_OK;


/**
 * Created by francescobruno on 04/04/17.
 */

public class ExpenseEditingFragment extends BaseFragment implements DatabaseReference.CompletionListener,
        DatePickerDialog.OnDateSetListener, IOnDataListener, ImageSourceDialog.IImageSourceDialogListener {


    public static class ExpenseEditingData {

        private static final String KEY_HAVE_DATA = "Have_data";
        private static final String KEY_NEW = "Is_New";
        private static final String KEY_MONEY_TRANSFER = "Is_Money_Transfer";
        private static final String KEY_SHARE_EVENLY = "Is_Share_Evenly";
        private static final String KEY_SHAACTKEY = "Share_Activity_Key";
        private static final String KEY_EXPKEY = "Expense_Key";
        private static final String KEY_PAYER = "Payer";
        private static final String KEY_DATE = "Date";
        private static final String KEY_CAPT_IMAGE = "CaptureImageUri";

        public Boolean haveData;
        public Boolean isNew;
        public Boolean isMoneyTransfer;
        public Boolean isSharedEvenly;
        public String shaActKey;
        public String expKey;
        public String payerId;
        public Date expenseDate;
        public String captureImageUri;
        public String mode;

        public ExpenseEditingData() {
            haveData = false;
            isNew = true;
            isMoneyTransfer = false;
            isSharedEvenly = true;
            shaActKey = null;
            expKey = null;
            payerId = null;
            expenseDate = new Date();
            captureImageUri = null;
            mode = AppConstants.NEW_MODE;
        }

        public void saveInstance(Bundle outState) {
            if (outState == null) return;
            outState.putBoolean(KEY_HAVE_DATA, haveData);
            outState.putBoolean(KEY_NEW, isNew);
            outState.putBoolean(KEY_MONEY_TRANSFER, isMoneyTransfer);
            outState.putBoolean(KEY_SHARE_EVENLY, isSharedEvenly);
            outState.putString(KEY_SHAACTKEY, shaActKey);
            outState.putString(KEY_EXPKEY, expKey);
            outState.putString(KEY_PAYER, payerId);
            outState.putSerializable(KEY_DATE, expenseDate);
            outState.putString(KEY_CAPT_IMAGE, captureImageUri);
            outState.putString(AppConstants.MODE, mode);
        }

        public void loadInstance(Bundle inState) {
            if (inState == null) return;
            haveData = inState.getBoolean(KEY_HAVE_DATA);
            isNew = inState.getBoolean(KEY_NEW);
            isMoneyTransfer = inState.getBoolean(KEY_MONEY_TRANSFER);
            isSharedEvenly = inState.getBoolean(KEY_SHARE_EVENLY);
            shaActKey = inState.getString(KEY_SHAACTKEY);
            expKey = inState.getString(KEY_EXPKEY);
            payerId = inState.getString(KEY_PAYER);
            expenseDate = (Date) inState.getSerializable(KEY_DATE);
            captureImageUri = inState.getString(KEY_CAPT_IMAGE);
            mode = inState.getString(AppConstants.MODE);
        }

    }


    private static final String DATE_PICKER_TAG = "date_picker";
    private static final String ERROR_DIALOG_TAG = "error_dialog";
    private static final String IMG_SOURCE_DIALOG_TAG = "image_source_dialog";

    private static final int RC_PHOTO_CAPTURE = 1;
    private static final int RC_PHOTO_REQUEST = 2;

    @BindView(R.id.rtv_expense_name)
    RequiredInputTextView mRtvExpenseName;
    @BindView(R.id.rtv_expense_description)
    RequiredInputTextView mRtvExpenseDescription;
    @BindView(R.id.rtv_expense_amount)
    RequiredInputTextView mRtvExpenseAmount;
    @BindView(R.id.rtv_expense_currency)
    RequiredInputTextView mRtvExpenseCurrency;
    @BindView(R.id.rtv_expense_payer)
    RequiredInputTextView mRtvExpensePayer;
    @BindView(R.id.rtv_amount_sharing)
    RequiredInputTextView mRtvAmountSharing;

    @BindView(R.id.img_expense_photo)
    ImageView mImage;
    @BindView(R.id.ed_expense_name)
    EditText mName;
    @BindView(R.id.ed_expense_description)
    EditText mDescription;
    @BindView(R.id.ed_expense_amount)
    EditText mAmount;
    @BindView(R.id.spin_expense_currency)
    Spinner mCurrency;
    @BindView(R.id.spin_expense_paidby)
    Spinner mPaidBySpinner;
    @BindView(R.id.tv_expense_date)
    TextView mTvDate;
    @BindView(R.id.sw_money_transfer)
    Switch mSwMoneyTransfer;
    @BindView(R.id.sw_share_evenly)
    Switch mSwShareEvenly;

    @BindView(R.id.ll_sharing_section)
    LinearLayout mLlSharingSection;
    @BindView(R.id.ll_sharing_info)
    LinearLayout mLlSharingInfo;

    private Unbinder mUnbinder;

    private UsersAdapter mUsersAdapter;
    private Expense newExpense;
    private ProgressDialog mProgressDialog;

    private ErrorDialog mErrorDialog;

    private ImageSourceDialog mImgSourceDialog;

    private DatePicker mDatePickerDialog;
    private DateFormat mDateFormat;

    private SharingActivityLoader mSharingActivityLoader;
    private ExpenseLoader mExpenseLoader;

    private ArrayList<User> mUsersList;

    private ExpenseEditingFragment.ExpenseEditingData eeData = new ExpenseEditingFragment.ExpenseEditingData();
    private Uri mUriSelectedImage, mUriCapturedImage;
    private String mExpKey;


    private SharingActivity mSharingActivity;
    private Expense mExpense;


    /*****************************************************
     *              PUBLIC METHODS                       *
     *****************************************************/

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.expense_editing_fragment, container, false);
        mUnbinder = ButterKnife.bind(this, view);

        if (savedInstanceState == null) {

            if ((getData(AppConstants.EXPENSE_KEY)) != null) {
                eeData.shaActKey = (String) ((Bundle) (getData(AppConstants.EXPENSE_KEY))).getSerializable(AppConstants.SHARING_ACTIVITY_KEY);
                eeData.expKey = (String) ((Bundle) (getData(AppConstants.EXPENSE_KEY))).getSerializable(AppConstants.EXPENSE_KEY);
                eeData.mode = (String) (((Bundle) (getData(AppConstants.EXPENSE_KEY))).getSerializable(AppConstants.MODE));
            } else {
                eeData.shaActKey = (String) ((Bundle) (getData(AppConstants.SHARING_ACTIVITY_KEY))).getSerializable(AppConstants.SHARING_ACTIVITY_KEY);
                eeData.expKey = null;
                eeData.mode = (String) (((Bundle) (getData(AppConstants.SHARING_ACTIVITY_KEY))).getSerializable(AppConstants.MODE));
            }

            eeData.isNew = (eeData.expKey == null);


            //eeData.expKey = (String) ((Bundle) (getData(AppConstants.EXPENSE_KEY))).getSerializable(AppConstants.EXPENSE_KEY);
            //(String) getData( AppConstants.EXPENSE_KEY );
            //eeData.isNew  = (eeData.expKey == null );
        } else {
            eeData.loadInstance(savedInstanceState);
        }

        mUsersList = new ArrayList<User>();
        mUsersAdapter = new UsersAdapter(getActivity(), mUsersList);
        mPaidBySpinner.setAdapter(mUsersAdapter);

        mSharingActivityLoader = new SharingActivityLoader();
        mSharingActivityLoader.setOnDataListener(this);
        mExpenseLoader = new ExpenseLoader();
        mExpenseLoader.setOnDataListener(this);

        mProgressDialog = new ProgressDialog(getActivity());
        mErrorDialog = new ErrorDialog();
        mImgSourceDialog = new ImageSourceDialog();
        createDatePickerDialog();

        return view;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
    }


    @Override
    public void onResume() {
        super.onResume();
        adjustActionBar();
        try {
            mProgressDialog.setTitle(R.string.lbl_loading_data);
            mProgressDialog.setMessage(getResources().getString(R.string.lbl_please_wait));
            mProgressDialog.show();
            mSharingActivityLoader.loadSharingActivity(eeData.shaActKey);
        } catch (DataLoaderException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        setHasOptionsMenu(false);
    }

    @Override
    public void onData(Object data) {
        if (data instanceof SharingActivity) {
            mSharingActivity = (SharingActivity) data;
            fillUsersSpinnerAndSharingSection();
            if (!eeData.isNew) {
                try {
                    mExpenseLoader.loadExpense(eeData.shaActKey, eeData.expKey);
                } catch (DataLoaderException e) {
                    e.printStackTrace();
                }
            } else {
                mExpense = null;
                mProgressDialog.dismiss();
                fillUserInterface();
            }
        } else if (data instanceof Expense) {
            mExpense = (Expense) data;
            eeData.expKey = mExpense.getKey();
            mProgressDialog.dismiss();
            fillUserInterface();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        eeData.haveData = true;
        eeData.saveInstance(outState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.expense_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save_expense:
                saveExpense();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @OnClick(R.id.img_expense_photo)
    public void pickExpensePhoto() {
        mImgSourceDialog.show(getChildFragmentManager(), IMG_SOURCE_DIALOG_TAG);
    }

    @OnClick(R.id.tv_expense_date)
    public void pickExpenseDate() {
        mDatePickerDialog.show(getFragmentManager(), DATE_PICKER_TAG);
    }

    @OnCheckedChanged(R.id.sw_money_transfer)
    public void moneyTransferChanged(boolean state) {
        eeData.isMoneyTransfer = state;
    }

    @OnCheckedChanged(R.id.sw_share_evenly)
    public void shareEvenlyChanged(boolean state) {
        eeData.isSharedEvenly = state;
        mLlSharingSection.setVisibility(state ? View.GONE : View.VISIBLE);
    }


    @Override
    public void onDateSet(android.widget.DatePicker datePicker, int year, int month, int date) {
        updateDate(year, month, date);
        mTvDate.setText(mDateFormat.format(eeData.expenseDate));
        mDatePickerDialog.setDate(eeData.expenseDate);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RC_PHOTO_REQUEST && resultCode == RESULT_OK && data != null) {
            eeData.captureImageUri = data.getData().toString();
            Glide.with(mImage.getContext()).load(eeData.captureImageUri).into(mImage);
        } else if (requestCode == RC_PHOTO_CAPTURE && resultCode == RESULT_OK) {
            Glide.with(mImage.getContext()).load(Uri.parse(eeData.captureImageUri)).into(mImage);
        }
    }


    @Override
    public void onImageSourceSelected(int which) {
        Intent intent;
        if (mImgSourceDialog != null) mImgSourceDialog.dismiss();
        if (which == ImageSourceDialog.TAKE_FROM_CAMERA) {
            intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            File image = null;
            File imageDir = null;
            try {
                imageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                image = File.createTempFile("expense_img", ".jpg", imageDir);
            } catch (IOException e) {
                Toast.makeText(getActivity(), R.string.lbl_camera_error, Toast.LENGTH_LONG).show();
                return;
            }
            if (image != null) {
                mUriCapturedImage = FileProvider.getUriForFile(getActivity(),
                        "it.polito.mad.countonme",
                        image);
                eeData.captureImageUri = mUriCapturedImage.toString();
            } else {
                Toast.makeText(getActivity(), R.string.lbl_camera_error, Toast.LENGTH_LONG).show();
                return;
            }
            intent.putExtra(MediaStore.EXTRA_OUTPUT, mUriCapturedImage);
            if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                startActivityForResult(intent, RC_PHOTO_CAPTURE);
            } else {
                Toast.makeText(getActivity(), R.string.lbl_camera_error, Toast.LENGTH_LONG).show();
            }
        } else {
            intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
            startActivityForResult(Intent.createChooser(intent, getResources().getString(R.string.lbl_select_picture)), RC_PHOTO_REQUEST);
        }

    }

    @Override
    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
        mProgressDialog.dismiss();
        if (databaseError != null) {
            StorageReference strRef = StorageManager.getInstance().getExpensesStorageReference(mExpKey);
            strRef.delete();
            mErrorDialog.setDialogContent(R.string.lbl_error_could_not_save, R.string.lbl_error_please_try_again);
            mErrorDialog.show(getFragmentManager(), ERROR_DIALOG_TAG);
        } else {
            clearForm();
            getFragmentManager().popBackStack();
        }
    }

    /*****************************************************
     *              PRIVATE METHODS                      *
     *****************************************************/


    private void fillUserInterface() {
        if (eeData.haveData) {
            if (eeData.captureImageUri != null)
                Glide.with(mImage.getContext()).load(Uri.parse(eeData.captureImageUri)).placeholder(R.drawable.ic_add_a_photo).crossFade().into(mImage);
            mSwMoneyTransfer.setChecked(eeData.isMoneyTransfer);
            mSwShareEvenly.setChecked(eeData.isSharedEvenly);
            // select the chosen payer
            String payerId;
            if (eeData.payerId != null) payerId = eeData.payerId;
            else payerId = ((CountOnMeApp) getActivity().getApplication()).getCurrentUser().getId();
            selectPayer(payerId);
        } else {
            if (eeData.isNew) {
                clearForm();
            } else {
                fillFormWithData();
            }
        }
    }


    private void fillUsersSpinnerAndSharingSection() {
        if (mSharingActivity == null) return;
        User user;
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        mUsersList.clear();
        mLlSharingInfo.removeAllViews();
        for (Map.Entry<String, User> entry : mSharingActivity.getUsers().entrySet()) {
            user = entry.getValue();
            mUsersList.add(user);
            // set the views for expenses sharing
            View child = inflater.inflate(R.layout.share_editing_item, null);
            ImageView userPhoto = (ImageView) child.findViewById(R.id.iv_user);
            TextView userName = (TextView) child.findViewById(R.id.tv_name);

            String namePhoto;
            StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();
            String imgUrl = user.getPhotoUrl();

            if (imgUrl != null && imgUrl.length() > 0) {
                Glide.with(userPhoto.getContext()).load(user.getPhotoUrl()).into(userPhoto);
            } else
                userPhoto.setImageResource(R.drawable.default_user_photo);

            userName.setText(user.getName());
            child.setTag(R.id.id_user, user);
            mLlSharingInfo.addView(child);
        }
        mUsersAdapter.notifyDataSetChanged();
    }


    private void createDatePickerDialog() {
        mDateFormat = new SimpleDateFormat(getString(R.string.fmt_date));
        mDatePickerDialog = new DatePicker();
        mDatePickerDialog.setDateSetListener(this);
        mDatePickerDialog.setDate(eeData.expenseDate);
    }


    private boolean checkData() {
        boolean dataProvided = true;

        if (TextUtils.isEmpty(mName.getText().toString())) {
            dataProvided = false;
            mRtvExpenseName.showError();
        } else {
            mRtvExpenseName.cleanError();
        }

        if (TextUtils.isEmpty(mDescription.getText().toString())) {
            dataProvided = false;
            mRtvExpenseDescription.showError();
        } else {
            mRtvExpenseDescription.cleanError();
        }

        Double amount;
        try {
            amount = Double.parseDouble(mAmount.getText().toString());
        } catch (NumberFormatException fne) {
            amount = 0.0;
            mAmount.setText("0");
        }

        if (TextUtils.isEmpty(mAmount.getText().toString())) {
            dataProvided = false;
            mRtvExpenseAmount.showError();
        } else {
            mRtvExpenseAmount.cleanError();
        }

        // check the sharing if needed
        if (eeData.isSharedEvenly == false) {
            Double total_sharing = 0.0;
            for (int idx = 0; idx < mLlSharingInfo.getChildCount(); idx++) {
                View view = mLlSharingInfo.getChildAt(idx);
                EditText edShareAmount = (EditText) view.findViewById(R.id.ed_amount);
                Double share_amount;
                try {
                    share_amount = Double.parseDouble(edShareAmount.getText().toString());
                } catch (NumberFormatException e) {
                    share_amount = 0.0;
                    edShareAmount.setText("0");
                }
                total_sharing += share_amount;
            }
            if (amount.compareTo(total_sharing) != 0) {
                dataProvided = false;
                mRtvAmountSharing.showError();
            } else {
                mRtvAmountSharing.cleanError();
            }
        }
        return dataProvided;
    }

    private void clearForm() {
        mName.setText("");
        mDescription.setText("");
        mAmount.setText("");
        // Set the currency to the sharing activity one
        mCurrency.setSelection(0);
        // SET the user to the current one
        mTvDate.setText(mDateFormat.format(new Date()));
        mSwMoneyTransfer.setChecked(false);
        mSwShareEvenly.setChecked(true);
    }

    private void adjustActionBar() {
        if (eeData.isNew)
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.expense_add_new_title);
        else
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.expense_details_title);
        setHasOptionsMenu(true);
    }

    private void updateDate(int year, int month, int date) {
        Calendar c = Calendar.getInstance();
        c.set(year, month, date);
        eeData.expenseDate.setTime(c.getTimeInMillis());
    }

    private void closeSoftKeyboard() {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
    }


    private void fillFormWithData() {
        try {
            eeData.captureImageUri = mExpense.getImageUrl();
            if (eeData.captureImageUri != null)
                Glide.with(mImage.getContext()).load(Uri.parse(eeData.captureImageUri)).placeholder(R.drawable.ic_add_a_photo).crossFade().into(mImage);
            mName.setText(mExpense.getName());
            mDescription.setText(mExpense.getDescription());
            mAmount.setText("" + mExpense.getAmount());
            // TODO manage the currency
            eeData.payerId = mExpense.getPayer().getId();
            selectPayer(eeData.payerId);
            eeData.expenseDate = mExpense.getDate();
            mTvDate.setText(mDateFormat.format(eeData.expenseDate));
            mDatePickerDialog.setDate(eeData.expenseDate);
            eeData.isMoneyTransfer = mExpense.getIsMoneyTransfer();
            mSwMoneyTransfer.setChecked(eeData.isMoneyTransfer);
            eeData.isSharedEvenly = mExpense.getIsSharedEvenly();
            mSwShareEvenly.setChecked(mExpense.getIsSharedEvenly());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void selectPayer(String payerId) {
        for (User user : mUsersList) {
            if (user.getId().equals(payerId)) {
                mPaidBySpinner.setSelection(mUsersList.indexOf(user));
                break;
            }
        }
    }


    private void saveExpense() {
        closeSoftKeyboard();
        if (checkData()) {
            mProgressDialog.setTitle(R.string.lbl_saving_expense);
            mProgressDialog.setMessage(getResources().getString(R.string.lbl_please_wait));
            mProgressDialog.show();

            if (eeData.isNew) {
                // first get the new expense Push Id
                DatabaseReference dbRef = DataManager.getsInstance().getSharActExpensesReference(eeData.shaActKey);
                mExpKey = dbRef.push().getKey();
            } else {
                mExpKey = eeData.expKey;
            }

            // look whether we have an image to save
            if (eeData.captureImageUri != null) {
                saveExpenseImage();
            } else {
                saveExpenseData(null);
            }

        }
    }

    private void saveExpenseData(Uri imageDownloadUrl) {
        newExpense = new Expense();
        newExpense.setKey(mExpKey);
        newExpense.setName(mName.getText().toString());
        newExpense.setDescription(mDescription.getText().toString());
        newExpense.setAmount(Double.valueOf(mAmount.getText().toString()));
        newExpense.setExpenseCurrency(mCurrency.getSelectedItem().toString());
        newExpense.setPayer((User) mPaidBySpinner.getSelectedItem());
        newExpense.setIsMoneyTransfer(eeData.isMoneyTransfer);
        newExpense.setIsSharedEvenly(eeData.isSharedEvenly);
        newExpense.setDate(eeData.expenseDate);
        newExpense.setParentSharingActivityId(eeData.shaActKey);
        newExpense.setCreatedBy(((CountOnMeApp) getActivity().getApplication()).getCurrentUser());
        if (imageDownloadUrl != null)
            newExpense.setImageUrl(imageDownloadUrl.toString());

        if (eeData.isSharedEvenly == false) {
            newExpense.clearShare();
            for (int idx = 0; idx < mLlSharingInfo.getChildCount(); idx++) {
                View view = mLlSharingInfo.getChildAt(idx);
                Double amount;
                try {
                    amount = Double.parseDouble(((EditText) view.findViewById(R.id.ed_amount)).getText().toString());
                } catch (NumberFormatException e) {
                    amount = 0.0;
                }
                User user = (User) view.getTag(R.id.id_user);
                newExpense.addShare(user.getId(), new Share(user, amount));
            }
        }

        // finally lets save data
        try {
            DataManager.getsInstance().updateExpense(eeData.shaActKey, newExpense, this);
        } catch (InvalidDataException ex) {
            mProgressDialog.dismiss();
            mErrorDialog.setDialogContent(R.string.lbl_error_could_not_save, R.string.lbl_error_please_try_again);
            mErrorDialog.show(getFragmentManager(), ERROR_DIALOG_TAG);

        }
    }


    private void saveExpenseImage() {
        StorageReference strRef = StorageManager.getInstance().getExpensesStorageReference(mExpKey);

        UploadTask uploadTask = strRef.putFile(Uri.parse(eeData.captureImageUri));

        uploadTask.addOnSuccessListener(getActivity(), new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                saveExpenseData(taskSnapshot.getDownloadUrl());
            }
        }).addOnFailureListener(getActivity(), new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                mProgressDialog.dismiss();
                mErrorDialog.setDialogContent(R.string.lbl_error_could_not_save, R.string.lbl_error_please_try_again);
                mErrorDialog.show(getFragmentManager(), ERROR_DIALOG_TAG);
            }
        });
    }


}
