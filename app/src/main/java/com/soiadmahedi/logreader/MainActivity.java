package com.soiadmahedi.logreader;

import android.animation.*;
import android.app.*;
import android.app.Activity;
import android.content.*;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.SharedPreferences;
import android.content.res.*;
import android.graphics.*;
import android.graphics.drawable.*;
import android.media.*;
import android.net.*;
import android.os.*;
import android.text.*;
import android.text.style.*;
import android.util.*;
import android.view.*;
import android.view.View;
import android.view.View.*;
import android.view.animation.*;
import android.webkit.*;
import android.widget.*;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import androidx.annotation.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.*;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.Adapter;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.card.*;
import java.io.*;
import java.text.*;
import java.util.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.*;
import org.json.*;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import android.app.AlertDialog.Builder;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import android.content.BroadcastReceiver;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import com.google.android.material.card.MaterialCardView;

public class MainActivity extends AppCompatActivity {
	
	private Timer _timer = new Timer();
	
	private Toolbar _toolbar;
	private AppBarLayout _app_bar;
	private CoordinatorLayout _coordinator;
	private final Pattern logPattern = Pattern.compile("^(.*\\d) ([VADEIW]) (.*): (.*)");
	private HashMap<String, Object> map = new HashMap<>();
	private BroadcastReceiver logger;
	private boolean autoscroll = false;
	private boolean oldlog = false;
	private String pkgFilter = "";
	private AlertDialog.Builder dailo;
	private String logActionText = "";
	private Parcelable scrollState;
	
	private ArrayList<HashMap<String, Object>> mainlist = new ArrayList<>();
	private ArrayList<String> pkgFilterList = new ArrayList<>();
	
	private LinearLayout linear_bg;
	private LinearLayout linear_option;
	private CoordinatorLayout linear_cordinate;
	private MaterialCardView card_auto_scroll;
	private CheckBox checkbox_i;
	private CheckBox checkbox_d;
	private CheckBox checkbox_a;
	private CheckBox checkbox_v;
	private CheckBox checkbox_w;
	private LinearLayout linear1;
	private TextView textview_auto_scroll;
	private Switch switch_auto_scroll;
	private RecyclerView recyclerview_log;
	
	private TimerTask it;
	private SharedPreferences save;
	private SharedPreferences sharedPreference;
	
	@Override
	protected void onCreate(Bundle _savedInstanceState) {
		super.onCreate(_savedInstanceState);
		setContentView(R.layout.main);
		initialize(_savedInstanceState);
		initializeLogic();
	}
	
	private void initialize(Bundle _savedInstanceState) {
		_app_bar = findViewById(R.id._app_bar);
		_coordinator = findViewById(R.id._coordinator);
		_toolbar = findViewById(R.id._toolbar);
		setSupportActionBar(_toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeButtonEnabled(true);
		_toolbar.setNavigationOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View _v) {
				onBackPressed();
			}
		});
		linear_bg = findViewById(R.id.linear_bg);
		linear_option = findViewById(R.id.linear_option);
		linear_cordinate = findViewById(R.id.linear_cordinate);
		card_auto_scroll = findViewById(R.id.card_auto_scroll);
		checkbox_i = findViewById(R.id.checkbox_i);
		checkbox_d = findViewById(R.id.checkbox_d);
		checkbox_a = findViewById(R.id.checkbox_a);
		checkbox_v = findViewById(R.id.checkbox_v);
		checkbox_w = findViewById(R.id.checkbox_w);
		linear1 = findViewById(R.id.linear1);
		textview_auto_scroll = findViewById(R.id.textview_auto_scroll);
		switch_auto_scroll = findViewById(R.id.switch_auto_scroll);
		recyclerview_log = findViewById(R.id.recyclerview_log);
		save = getSharedPreferences("save", Activity.MODE_PRIVATE);
		sharedPreference = getSharedPreferences("Save Data", Activity.MODE_PRIVATE);
		
		card_auto_scroll.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View _view) {
				switch_auto_scroll.performClick();
			}
		});
		
		switch_auto_scroll.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton _param1, boolean _param2) {
				final boolean _isChecked = _param2;
				if (_isChecked) {
					autoscroll = true;
					recyclerview_log.smoothScrollToPosition((int)mainlist.size());
				}
				else {
					autoscroll = false;
				}
			}
		});
	}
	
	private void initializeLogic() {
		checkbox_i.setChecked(true);
		checkbox_d.setChecked(true);
		checkbox_a.setChecked(true);
		checkbox_v.setChecked(true);
		checkbox_w.setChecked(true);
		switch_auto_scroll.setChecked(true);
		autoscroll = true;
		recyclerview_log.setLayoutManager(new LinearLayoutManager(this));
		logActionText = "com.soiadmahedi.logreader.ACTION_NEW_DEBUG_LOG";
		logger = new BroadcastReceiver() {
			 @Override
			public void onReceive(Context context, Intent intent) {
				if (intent.getAction().equals(logActionText)) {
					String log = intent.getStringExtra("log");
					if (log != null) {
						map = new HashMap<>();
						map.put("logRaw", intent.getStringExtra("log"));
						if (intent.hasExtra("packageName")) {
							map.put("pkgName", intent.getStringExtra("packageName"));
						}
						else {
							
						}
						Matcher matcher = logPattern.matcher(intent.getStringExtra("log"));
						if (matcher.matches()) {
							map.put("date", matcher.group(1).trim());
							map.put("type", matcher.group(2).trim());
							map.put("header", matcher.group(3).trim());
							map.put("body", matcher.group(4).trim());
							map.put("culturedLog", "true");
							String typeText = matcher.group(2).trim();
							if (typeText.equals("I")) {
								if (checkbox_i.isChecked()) {
									mainlist.add(map);
								}
							}
							else {
								if (typeText.equals("D")) {
									if (checkbox_d.isChecked()) {
										mainlist.add(map);
									}
								}
								else {
									if (typeText.equals("A")) {
										if (checkbox_a.isChecked()) {
											mainlist.add(map);
										}
									}
									else {
										if (typeText.equals("V")) {
											if (checkbox_v.isChecked()) {
												mainlist.add(map);
											}
										}
										else {
											if (typeText.equals("W")) {
												if (checkbox_w.isChecked()) {
													mainlist.add(map);
												}
											}
											else {
												mainlist.add(map);
											}
										}
									}
								}
							}
						}
						else {
							mainlist.add(map);
						}
						scrollState = recyclerview_log.getLayoutManager().onSaveInstanceState();
						recyclerview_log.setAdapter(new Recyclerview_logAdapter(mainlist));
						recyclerview_log.getLayoutManager().onRestoreInstanceState(scrollState);
						if (autoscroll) {
							recyclerview_log.smoothScrollToPosition((int)mainlist.size());
						}
					}
				}
			}
		};
	}
	
	@Override
	public void onStart() {
		super.onStart();
		IntentFilter filter = new IntentFilter("com.soiadmahedi.logreader.ACTION_NEW_DEBUG_LOG");
		registerReceiver(logger, filter);
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		unregisterReceiver(logger);
	}
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuItem menuPackage = menu.add(Menu.NONE, 0, Menu.NONE, "Packages");
		menuPackage.setIcon(R.drawable.icon_science_baseline);
		menuPackage.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
		MenuItem menuLogs = menu.add(Menu.NONE, 1, Menu.NONE, "Log Target");
		menuLogs.setIcon(R.drawable.icon_filter_alt_baseline);
		menuLogs.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
		MenuItem menuExit = menu.add(Menu.NONE, 2, Menu.NONE, "Exit");
		menuExit.setIcon(R.drawable.icon_exit_to_app_baseline);
		menuExit.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
		MenuItem menuClear = menu.add(Menu.NONE, 3, Menu.NONE, "Clear Log");
		menuClear.setIcon(R.drawable.icon_clear_all_baseline);
		menuClear.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		MenuItem menuScroll = menu.add(Menu.NONE, 4, Menu.NONE, "Scroll Last");
		menuScroll.setIcon(R.drawable.icon_keyboard_double_arrow_down_baseline);
		menuScroll.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		final int _id = item.getItemId();
		final String _title = (String) item.getTitle();
		switch((int)_id) {
			case ((int)0): {
				 
				break;
			}
			case ((int)1): {
				 
				break;
			}
			case ((int)2): {
				finish();
				break;
			}
			case ((int)3): {
				mainlist.clear();
				recyclerview_log.setAdapter(new Recyclerview_logAdapter(mainlist));
				break;
			}
			case ((int)4): {
				recyclerview_log.smoothScrollToPosition((int)mainlist.size());
				break;
			}
		}
		return super.onOptionsItemSelected(item);
	}
	public class Recyclerview_logAdapter extends RecyclerView.Adapter<Recyclerview_logAdapter.ViewHolder> {
		
		ArrayList<HashMap<String, Object>> _data;
		
		public Recyclerview_logAdapter(ArrayList<HashMap<String, Object>> _arr) {
			_data = _arr;
		}
		
		@Override
		public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
			LayoutInflater _inflater = getLayoutInflater();
			View _v = _inflater.inflate(R.layout.view, null);
			RecyclerView.LayoutParams _lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
			_v.setLayoutParams(_lp);
			return new ViewHolder(_v);
		}
		
		@Override
		public void onBindViewHolder(ViewHolder _holder, final int _position) {
			View _view = _holder.itemView;
			
			final com.google.android.material.card.MaterialCardView cardview = _view.findViewById(R.id.cardview);
			final LinearLayout linear_bg = _view.findViewById(R.id.linear_bg);
			final LinearLayout linear_type = _view.findViewById(R.id.linear_type);
			final LinearLayout linear_log_body = _view.findViewById(R.id.linear_log_body);
			final TextView textview_log_type = _view.findViewById(R.id.textview_log_type);
			final TextView textview_log_header = _view.findViewById(R.id.textview_log_header);
			final TextView textview_log_date = _view.findViewById(R.id.textview_log_date);
			final TextView textview_log_body = _view.findViewById(R.id.textview_log_body);
			final TextView textview_log_package = _view.findViewById(R.id.textview_log_package);
			
			if (_data.get((int)_position).containsKey("culturedLog")) {
				if (_data.get((int)_position).get("type").toString().equals("I")) {
					linear_bg.setBackgroundColor(0xFFDCEDC8);
					linear_type.setBackgroundColor(0xFF8BC34A);
				}
				if (_data.get((int)_position).get("type").toString().equals("D")) {
					linear_bg.setBackgroundColor(0xFFB3E5FC);
					linear_type.setBackgroundColor(0xFF03A9F4);
				}
				if (_data.get((int)_position).get("type").toString().equals("A")) {
					linear_bg.setBackgroundColor(0xFFE1BEE7);
					linear_type.setBackgroundColor(0xFF9C27B0);
				}
				if (_data.get((int)_position).get("type").toString().equals("E")) {
					linear_bg.setBackgroundColor(0xFFFFCDD2);
					linear_type.setBackgroundColor(0xFFF44336);
				}
				if (_data.get((int)_position).get("type").toString().equals("V")) {
					linear_bg.setBackgroundColor(0xFFCFD8DC);
					linear_type.setBackgroundColor(0xFF607D8B);
				}
				if (_data.get((int)_position).get("type").toString().equals("W")) {
					linear_bg.setBackgroundColor(0xFFFFF9C4);
					linear_type.setBackgroundColor(0xFFFFEB3B);
				}
				if (pkgFilterList.isEmpty()) {
					textview_log_type.setText(_data.get((int)_position).get("type").toString());
					textview_log_header.setText(_data.get((int)_position).get("header").toString());
					textview_log_date.setText(_data.get((int)_position).get("date").toString());
					textview_log_body.setText(_data.get((int)_position).get("body").toString());
					textview_log_package.setText(_data.get((int)_position).get("pkgName").toString());
				}
				else {
					if (pkgFilterList.contains(_data.get((int)_position).get("pkgName").toString())) {
						textview_log_type.setText(_data.get((int)_position).get("type").toString());
						textview_log_header.setText(_data.get((int)_position).get("header").toString());
						textview_log_date.setText(_data.get((int)_position).get("date").toString());
						textview_log_body.setText(_data.get((int)_position).get("body").toString());
						textview_log_package.setText(_data.get((int)_position).get("pkgName").toString());
					}
					else {
						linear_bg.setVisibility(View.GONE);
					}
				}
				linear_bg.setOnLongClickListener(new View.OnLongClickListener() {
					@Override
					public boolean onLongClick(View _view) {
						((ClipboardManager) getSystemService(getApplicationContext().CLIPBOARD_SERVICE)).setPrimaryClip(ClipData.newPlainText("clipboard", _data.get((int)_position).get("header").toString().concat(_data.get((int)_position).get("body").toString())));
						SketchwareUtil.showMessage(getApplicationContext(), "copied to clipboard");
						return true;
					}
				});
			}
			else {
				if (_data.get((int)_position).containsKey("logRaw")) {
					textview_log_type.setText("E");
					textview_log_header.setText(_data.get((int)_position).get("logRaw").toString());
					textview_log_date.setVisibility(View.GONE);
					textview_log_body.setVisibility(View.GONE);
					textview_log_package.setVisibility(View.GONE);
					linear_bg.setBackgroundColor(0xFFFFCDD2);
					linear_type.setBackgroundColor(0xFFF44336);
					linear_bg.setOnLongClickListener(new View.OnLongClickListener() {
						@Override
						public boolean onLongClick(View _view) {
							((ClipboardManager) getSystemService(getApplicationContext().CLIPBOARD_SERVICE)).setPrimaryClip(ClipData.newPlainText("clipboard", _data.get((int)_position).get("logRaw").toString()));
							SketchwareUtil.showMessage(getApplicationContext(), "copied to clipboard ");
							return true;
						}
					});
				}
				else {
					linear_bg.setVisibility(View.GONE);
				}
			}
		}
		
		@Override
		public int getItemCount() {
			return _data.size();
		}
		
		public class ViewHolder extends RecyclerView.ViewHolder {
			public ViewHolder(View v) {
				super(v);
			}
		}
	}
}