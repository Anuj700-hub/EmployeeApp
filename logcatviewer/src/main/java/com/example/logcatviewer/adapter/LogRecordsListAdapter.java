package com.example.logcatviewer.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.logcatviewer.R;
import com.example.logcatviewer.utils.Constants;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * This is the list adapter for the saved logcat records.
 */
public class LogRecordsListAdapter extends BaseAdapter {
    //'Saved Log' files
    private File mLogRecordsFiles[];
    private Context mContext;
    private LayoutInflater mInflater;

    //Viewholder to hold view.
    private class ViewHolder {
        public TextView mTvRecordFilename;
        public TextView mTvRecordFileLastModified;
        public TextView mTvRecordFileSize;
    }

    public LogRecordsListAdapter(Context context) {
        mContext = context;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        readAllRecordsFiles();
    }


    @Override
    public int getCount() {
        return mLogRecordsFiles.length;
    }

    @Override
    public Object getItem(int i) {
        return mLogRecordsFiles[i];
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public boolean isEmpty() {
        return mLogRecordsFiles.length == 0;
    }

    @Override
    public View getView(int pos, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        File file = mLogRecordsFiles[pos];

        if (convertView == null) { //inflate the view here because there's no existing view object.
            convertView = mInflater.inflate(R.layout.logrecord_listitem, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.mTvRecordFilename = (TextView) convertView.findViewById(R.id.recordFilename);
            viewHolder.mTvRecordFileLastModified = (TextView) convertView.findViewById(R.id.recordFileLastModified);
            viewHolder.mTvRecordFileSize = (TextView) convertView.findViewById(R.id.recordFileSize);

            convertView.setTag(viewHolder);

        } else { //reuse existing view
            viewHolder = (ViewHolder) convertView.getTag();
        }


        viewHolder.mTvRecordFilename.setText(file.getName());

        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss", Locale.US);
        viewHolder.mTvRecordFileLastModified.setText(formatter.format(new Date(file.lastModified())));

        viewHolder.mTvRecordFileSize.setText(getRecordFileSize(file.length()));

        return convertView;
    }

    @Override
    public void notifyDataSetChanged() {
        //Always read all entries before updating list-view.
        readAllRecordsFiles();
        super.notifyDataSetChanged();
    }

    /**
     * Read all 'saved logs' files from the directory.
     */
    private void readAllRecordsFiles() {
        File logRecordDir = Constants.getRecordDir(mContext);
        mLogRecordsFiles = logRecordDir.listFiles();
        if (mLogRecordsFiles == null) {
            mLogRecordsFiles = new File[0];
        }
    }

    /**
     * Convert file size in KB or MB and return,
     *
     * @param fileSize file size in bytes.
     * @return file size to two decimals in proper unit: KB if less than 1 KB else in MB.
     */
    private String getRecordFileSize(long fileSize) {
        String fileSizeUnit = fileSize / 1048576 == 0 ? "KB" : "MB";
        double size = fileSizeUnit.compareTo("KB") == 0 ? fileSize / 1024.0 : fileSize / 1048576.0;
        return String.format("%.2f " + fileSizeUnit, size);
    }
}
