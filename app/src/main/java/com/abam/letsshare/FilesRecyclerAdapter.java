/*
MADE BY ASWANTH VC
******************
Copy Righted content dont edit or republish
© ABAM
*/
package com.abam.letsshare;
import android.support.v7.widget.*;
import java.util.*;
import android.view.*;
import android.view.View.*;
import android.content.*;
import android.widget.*;
import java.io.*;
import android.net.*;
import android.webkit.*;
import android.graphics.*;
import android.provider.*;
import android.media.*;
import android.util.*;
import android.graphics.drawable.*;

public class FilesRecyclerAdapter extends RecyclerView.Adapter<FilesRecyclerAdapter.ViewHolder>
{
	private Context context;
	private List<File> mData = new ArrayList<File>();
	public boolean showHiddenFiles = false;
	private ItemClickListener listener;
	public String TAG = "FILES_RECYCLER_ADAPTER";
	// Initialize
	public FilesRecyclerAdapter(Context c,List<File> k){
		context = c;
		mData = k;
	}
	// Create view holder for poition
	@Override
	public ViewHolder onCreateViewHolder(ViewGroup p1, int p2)
	{
		View v = LayoutInflater.from((p1.getContext())).inflate(R.layout.files_view_1,p1,false);
		return new ViewHolder(v);
	}
	// Code to execute when holdee is attached
	@Override
	public void onBindViewHolder(ViewHolder holder, int position)
	{
		// Get data
		File data = mData.get(position);
		// Extension of file
		String extension = MimeTypeMap.getFileExtensionFromUrl(data.getAbsolutePath());
		String type = null;
		//Uri uri = Uri.fromFile(data);
		if(extension != null) type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
		if(type == null) type = "Unknown";
		if(data.isDirectory()){
			// File is directory
			holder.thumbnail.setBackgroundResource(R.drawable.folder_image);
			holder.FileType.setText("Folder");
		}else if(type.startsWith("image/")){
			// File is image
			// Set image thumbnail
			// Bitmap imgs = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(data.getAbsolutePath()),70,70);
			Drawable im = Drawable.createFromPath(data.getAbsolutePath());
			holder.thumbnail.setBackgroundDrawable(im);
			holder.FileType.setText("Image");
		}else if(type.startsWith("application/pdf")){
			// File is pdf
			// Set pdf drawable
			holder.FileType.setText("PDF");
			holder.thumbnail.setBackgroundResource(R.drawable.pdf_file);
		}
		else{
			// File is some another unknown file
			// set thumbnail as file
			holder.thumbnail.setBackgroundResource(R.drawable.unknown_file);
			holder.FileType.setText(extension);
		}
		holder.filename.setText(data.getName());
		if(listener != null) listener.selectionHighlight(data,holder);
	}
	// Main view holder class
	public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
		TextView filename;
		ImageView thumbnail;
		TextView FileType;
		LinearLayout mainLayout;
		public ViewHolder(View v){
			super(v);
			filename = v.findViewById(R.id.files_view_filename);
			thumbnail = v.findViewById(R.id.files_view_Thumbnail);
			FileType = v.findViewById(R.id.files_view_FileType);
			mainLayout = v.findViewById(R.id.files_view_1LinearLayout);
			v.setOnClickListener(this);
		}
		@Override public void onClick(View view){
			int position  = getAdapterPosition();
			listener.onClick(position,mData.get(position),this);
		}
	}

	@Override
	public long getItemId(int position)
	{
		return position;
	}
	@Override
	public int getItemViewType(int position)
	{
		return position;
	}
	// Get item count
	@Override
	public int getItemCount()
	{
		return (mData != null) ? mData.size() : 0;
	}
	
	// Set click listener
	public void setClickListener(ItemClickListener i){
		listener = i;
	}
	// Click interface
	public interface ItemClickListener{
		void onClick(int position,File file,ViewHolder v);
		void onBindFinished();
		void selectionHighlight(File file,ViewHolder v);
	}
}
