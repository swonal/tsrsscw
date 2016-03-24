package com.tsrsscw.feed;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import com.tsrsscw.feed.data.RssFeedItem;
import com.tsrsscw.feed.util.XMLPullParserHandler;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;
 
public class DisplayItems extends Activity {
 
    ListView listView;
    String xml;
    private List<RssFeedItem> rssItems = new ArrayList<RssFeedItem>();
    Bundle extras;
    //Indicate which feed was selected
    int id;
	ProgressDialog progress;
	ArrayAdapter<RssFeedItem> adapter;
	SearchView sv;
     
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.feed_show);
         
        Log.e("tag", "beginning");
        
        listView = (ListView) findViewById(R.id.list);
        sv =(SearchView)findViewById(R.id.sv);
        extras = getIntent().getExtras();
        id = extras.getInt("id");
        
        new GetRssTask().execute();
        
        sv.setOnQueryTextListener(new OnQueryTextListener()
		{
			@Override 
			public boolean onQueryTextChange(String text) 
			{
				Log.e("Tag", text);
				text=text.toString().toLowerCase(Locale.getDefault());
				((MyListAdapter) adapter).filter(text);
				return false;
			}
			@Override
			public boolean onQueryTextSubmit(String text) 
			{
				return false;
			}	
		});
        
        listView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?>parent, View view, int position, long rowid){
				parent.getItemAtPosition(position);
				String tempDesc = adapter.getItem(position).getDescription();
				Toast.makeText(DisplayItems.this, "Clicked " + position + "\n" + tempDesc, Toast.LENGTH_LONG).show();
				
				Intent intent = new Intent(getApplicationContext(), Detail.class);
				intent.putExtra("title", adapter.getItem(position).getTitle());
				intent.putExtra("desc", adapter.getItem(position).getDescription());
				intent.putExtra("sd", adapter.getItem(position).getStartDate());
				intent.putExtra("ed", adapter.getItem(position).getEndDate());
				intent.putExtra("link", adapter.getItem(position).getLink());
				intent.putExtra("gps", adapter.getItem(position).getGps());
				startActivity(intent);
			}
		});
        
        
        
        Log.e("tag", "before parse");
        
        


    }
    
    
 


	private class MyListAdapter extends ArrayAdapter<RssFeedItem> {
		private List<RssFeedItem> feeds;
		private ArrayList<RssFeedItem> filtered;
		
    	public MyListAdapter(){
    		super(DisplayItems.this, R.layout.item_view, rssItems);
    		
    		this.feeds = rssItems;
    		this.filtered = new ArrayList<RssFeedItem>();
    		this.filtered.addAll(feeds);
    	}
		
    	public View getView(int position, View convertView, ViewGroup parent){
    		//make sure there is a view
    		View itemView = convertView;
    		if (itemView == null){
    			itemView = getLayoutInflater().inflate(R.layout.item_view, parent, false);
    		}
    		
    		//find the feed
    		RssFeedItem currentFeed = rssItems.get(position);
    		
    		//title
    		TextView titleText = (TextView) itemView.findViewById(R.id.txtTitle);
    		titleText.setText(currentFeed.getTitle());
    		
    		//description
    		TextView descText = (TextView) itemView.findViewById(R.id.txtDesc);
    		descText.setText(currentFeed.getDescription());
    		
    		//startDate
    		TextView stdText = (TextView) itemView.findViewById(R.id.txtStartDate);
    		stdText.setText("Start Date: " + currentFeed.getStartDate());
    		
    		//EndDate
    		TextView endText = (TextView) itemView.findViewById(R.id.txtEndDate);
    		endText.setText("End Date: " + currentFeed.getEndDate());
    		
    		//Link
    		TextView pubDateText = (TextView) itemView.findViewById(R.id.txtLink);
    		pubDateText.setText(currentFeed.getLink());
    		Log.e("tag", "before return itemView");
    		return itemView;
    	}
    	
    	//filter the input data against the title
    	public void filter(String charText) {
    		charText = charText.toLowerCase(Locale.getDefault());
    		feeds.clear();
    		if (charText.length() == 0) {
    			feeds.addAll(filtered);
    		} 
    		else 
    		{
    			for (RssFeedItem rfi : filtered) 
    			{
    				if (rfi.getTitle().toLowerCase(Locale.getDefault()).contains(charText)) //used title tag here
    				{
    					feeds.add(rfi);
    				}
    			}
    		}
    		sortAlpha(); //resort them in alphabetical order
    		notifyDataSetChanged();
    	}
	}
	
	
	public void sortAlpha(){
		Collections.sort(rssItems, new Comparator<RssFeedItem>(){
		    public int compare(RssFeedItem rss1, RssFeedItem rss2) {
		        return rss1.getTitle().compareToIgnoreCase(rss2.getTitle());
		    }
		});
	}

	private static String sourceListingString(String urlString)throws IOException
    {
	 	String result = "";
		InputStream anInStream = null;
    	int response = -1;
    	URL url = new URL(urlString);
    	URLConnection conn = url.openConnection();
    	
    	Log.e("tag", "in inputstream 1" + url);
    	
    	// Check that the connection can be opened
    	if (!(conn instanceof HttpURLConnection))
    			throw new IOException("Not an HTTP connection");
    	try
    	{
    		// Open connection
    		Log.e("tag", "in inputstream 1.1");
    		HttpURLConnection httpConn = (HttpURLConnection) conn;
    		Log.e("tag", "in inputstream 1.2");
    		httpConn.setAllowUserInteraction(false);
    		Log.e("tag", "in inputstream 1.3");
    		httpConn.setInstanceFollowRedirects(true);
    		Log.e("tag", "in inputstream 1.4");
    		httpConn.setRequestMethod("GET");
    		Log.e("tag", "in inputstream 1.5");
    		try{
    		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
    		StrictMode.setThreadPolicy(policy);
    		httpConn.connect();
    		}catch (Exception ec){
    			Log.e("tag", "in inputstream 1.5" + ec);
    		}
    		Log.e("tag", "in inputstream 1.6");
    		response = httpConn.getResponseCode();
    		
    		Log.e("tag", "in inputstream 2: " + response);
    		
    		// Check that connection is Ok
    		if (response == HttpURLConnection.HTTP_OK)
    		{
    			// Connection is Ok so open a reader 
    			anInStream = httpConn.getInputStream();
    			InputStreamReader in= new InputStreamReader(anInStream);
    			BufferedReader bin= new BufferedReader(in);
    			
    			Log.e("tag", "in inputstream 3");
    			
    			// Read in the data from the RSS stream
    			String line = new String();
    			// Read past the RSS headers
    			bin.readLine();
    			//bin.readLine();
    			// Keep reading until there is no more data
    			while (( (line = bin.readLine())) != null)
    			{
    				
    				result = result + "\n" + line;
    			}
    		}
    	}                
    	catch (Exception ex)
    	{
    			throw new IOException("Error connecting");
    	}
    	Log.e("tag", "in inputstream 4");
    	// Return result as a string for further processing
    	return result;
    	
    } // End of sourceListingString
    
private class GetRssTask extends AsyncTask<Void, String, Void>{
	@Override
	protected void onPreExecute() {
		progress = new ProgressDialog(DisplayItems.this);
		progress.setTitle("Retreiving RSS");
		progress.setMessage("Loading RSS... \nTypically less than 10 seconds");
		progress.setCancelable(false);
		progress.setCanceledOnTouchOutside(false);
		progress.show();
	}
	
	@Override
		protected Void doInBackground(Void... params) {
		try {
			xml = sourceListingString(extras.getString("ci"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		publishProgress("Download Rss Feed Successful \nParsing...");

        XMLPullParserHandler parser = new XMLPullParserHandler();
		rssItems = parser.parse(xml, id);
		publishProgress("Parsing Successful");

			return null;
		}
	
	@Override
		protected void onProgressUpdate(String... values) {
		progress.setMessage(values[0]);
		}
	
	@Override
		protected void onPostExecute(Void result) {
		adapter = new MyListAdapter();
		
		sortAlpha(); //sort the items in alphabetical order

		listView.setAdapter(adapter);
		
		progress.dismiss();
		
		Toast.makeText(DisplayItems.this, "Completed", Toast.LENGTH_LONG).show();
		
		
		}
}
    
 
}