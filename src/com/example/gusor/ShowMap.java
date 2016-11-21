package com.example.gusor;

import java.util.ArrayList;
import java.util.List;

import com.example.gusor.utils.*;

import android.app.Activity;
import android.content.pm.ApplicationInfo;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.SlidingDrawer;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.AMapLocationClientOption.AMapLocationMode;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMap.InfoWindowAdapter;
import com.amap.api.maps.AMap.OnInfoWindowClickListener;
import com.amap.api.maps.AMap.OnMapClickListener;
import com.amap.api.maps.AMap.OnMarkerClickListener;
import com.amap.api.maps.AMapOptions;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.SupportMapFragment;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.LocationSource.OnLocationChangedListener;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.CircleOptions;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.core.SuggestionCity;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.amap.api.services.poisearch.PoiSearch.OnPoiSearchListener;
import com.amap.api.services.poisearch.PoiSearch.SearchBound;
import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.baoyz.swipemenulistview.SwipeMenuListView.OnMenuItemClickListener;
import com.example.gusor.entity.Contentinfo;
import com.example.gusor.utils.Constants;


public class ShowMap extends Activity implements LocationSource,AMapLocationListener,OnMapClickListener,OnMarkerClickListener,
OnPoiSearchListener,OnClickListener,OnInfoWindowClickListener, InfoWindowAdapter{
	
	
	
	
	private RelativeLayout mPoiDetail;
	private PoiResult poiResult; // poi返回的结果
	private int currentPage = 0;// 当前页面，从0开始计数
	private PoiSearch.Query query;// Poi查询条件类
	private LatLonPoint lp = new LatLonPoint(32.08425835503472,  118.77954915364583);// 



	private Marker locationMarker; // 选择的点
	private Marker detailMarker;
	private Marker mlastMarker;
	private PoiSearch poiSearch;
	private myPoiOverlay poiOverlay;// poi图层
	private List<PoiItem> poiItems;// poi数据
	private PoiItem mPoi;
	private EditText mSearchText;
	private TextView mPoiName, mPoiAddress;
	private String keyWord = "";




	private MapView mapView;//地图的view
	private OnLocationChangedListener mListener;//监听
	private AMapLocationClient mlocationClient;
	private AMapLocationClientOption mLocationOption;
	MyAdapter adapter;//自定义adapter
	private AMap aMap;//地图
	private static SlidingDrawer slid;//上拉框
	private SwipeMenuListView list;//上拉框中的listview
	private TextView btn_search;//搜索按钮
	private TextView tvcount;
	private List<Contentinfo> infos=new ArrayList<Contentinfo>();//内容集合
	protected void onCreate(Bundle savedInstanceState) {
		// TODO 自动生成的方法存根
		super.onCreate(savedInstanceState);
		Log.i("fjs", "进入onCreate");
		setContentView(R.layout.basicmap_activity);
		
		tvcount=(TextView)findViewById(R.id.findcount);
		mapView = (MapView) findViewById(R.id.mapView);
		mapView.onCreate(savedInstanceState);// 此方法必须重写
		initMap();
		slid=(SlidingDrawer)findViewById(R.id.sliddraw);
		list=(SwipeMenuListView)findViewById(R.id.listview);
		
		// step 1. create a MenuCreator
				SwipeMenuCreator creator = new SwipeMenuCreator() {

					@Override
					public void create(SwipeMenu menu) {
						
						
						// create "delete" item
						SwipeMenuItem deleteItem = new SwipeMenuItem(
								getApplicationContext());
						// set item background
						deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9,
								0x3F, 0x25)));
						// set item width
						deleteItem.setWidth(dp2px(90));
						// set a icon
						deleteItem.setIcon(R.drawable.ic_delete);
						// add to menu
						menu.addMenuItem(deleteItem);
					}
				};
				// set creator
				list.setMenuCreator(creator);

				// step 2. listener item click event
				list.setOnMenuItemClickListener(new OnMenuItemClickListener() {
					@Override
					public void onMenuItemClick(int position, SwipeMenu menu, int index) {
				
							infos.remove(position);
							adapter.notifyDataSetChanged();
						
						
					}
				});
				
		btn_search=(TextView)findViewById(R.id.btn_search);
		
//		btn_search.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				slid.setVisibility(View.VISIBLE);
//				slid.open();
//			}
//		});
	}

	
	
	//使上拉框可见并展开
	public  void showSlid(){
		slid.setVisibility(View.VISIBLE);
		slid.open();
	}
	//初始化定位
	private void setUpMap() {
		Log.i("fjs", "进入setUpMap");
		aMap.setLocationSource(this);// 设置定位监听
		aMap.getUiSettings().setMyLocationButtonEnabled(true);// 设置默认定位按钮是否显示
		aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
		// 设置定位的类型为定位模式 ，可以由定位、跟随或地图根据面向方向旋转几种
		aMap.setMyLocationType(AMap.LOCATION_TYPE_LOCATE);

	}

		

	/**
	 * 初始化AMap对象
	 */
	private void initMap() {
		Log.i("fjs", "进入initMap");
		if (aMap == null) {
			aMap = mapView.getMap();// amap对象初始化成功
			aMap.setOnMapClickListener(this);
			aMap.setOnMarkerClickListener(this);
			aMap.setOnInfoWindowClickListener(this);
			aMap.setInfoWindowAdapter(this);
			TextView searchButton = (TextView) findViewById(R.id.btn_search);
			searchButton.setOnClickListener(this);
	

			setUpMap();
		}
		setup();
	}
	@Override
	public void activate(OnLocationChangedListener listener) {
		Log.i("fjs", "进入activate");
		mListener = listener;
		if (mlocationClient == null) {
			mlocationClient = new AMapLocationClient(this);
			mLocationOption = new AMapLocationClientOption();
			//设置定位监听
			mlocationClient.setLocationListener(this);
			//设置为高精度定位模式
			mLocationOption.setLocationMode(AMapLocationMode.Hight_Accuracy);
			//设置定位参数
			mlocationClient.setLocationOption(mLocationOption);
			// 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
			mLocationOption.setInterval(2000);
			// 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用stopLocation()方法来取消定位请求
			// 在定位结束后，在合适的生命周期调用onDestroy()方法
			// 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
			mlocationClient.startLocation();
		}

	}
	/**
	 * 方法必须重写
	 */
	@Override
	protected void onResume() {
		super.onResume();
		Log.i("fjs", "进入onResume");
		mapView.onResume();
		whetherToShowDetailInfo(false);
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onPause() {
		super.onPause();
		mapView.onPause();
		Log.i("fjs", "进入onPause");
		deactivate();
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		Log.i("fjs", "进入onSaveInstanceState");
		mapView.onSaveInstanceState(outState);
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onDestroy() {
		super.onDestroy();
		mapView.onDestroy();
		if(null != mlocationClient){
			mlocationClient.onDestroy();
		}
	}

	@Override
	public void deactivate() {
		Log.i("fjs", "进入deactivate");
		mListener = null;
		if (mlocationClient != null) {
			mlocationClient.stopLocation();
			mlocationClient.onDestroy();
		}
		mlocationClient = null;

	}
	@Override
	public void onLocationChanged(AMapLocation amapLocation) {
		
		lp = new LatLonPoint(amapLocation.getLatitude(),amapLocation.getLongitude());
		
		mListener.onLocationChanged(amapLocation);// 显示系统小蓝点
		
		

	}
	
	
	/**
	 * 周边
	 */
	
	
	
	
	
	private void whetherToShowDetailInfo(boolean isToShow) {
		if (isToShow) {
			mPoiDetail.setVisibility(View.VISIBLE);

		} else {
			mPoiDetail.setVisibility(View.GONE);

		}
	}
	
	
	@Override
	public void onPoiSearched(PoiResult result, int rcode) {
		if (rcode == 1000) {
			if (result != null && result.getQuery() != null) {// 搜索poi的结果
				if (result.getQuery().equals(query)) {// 是否是同一条
					poiResult = result;
					poiItems = poiResult.getPois();// 取得第一页的poiitem数据，页数从数字0开始
					infos.clear();
					if(infos.size()==0){
					for (int i = 0; i < poiItems.size(); i++) {
						String tel=poiItems.get(i).getTel();
						
						if(!(tel.length()>0)){
							tel="暂无";
						}
						Contentinfo info=new Contentinfo(R.drawable.thefood,poiItems.get(i).getTitle(), poiItems.get(i).getAdName(),poiItems.get(i).getBusinessArea(), "$10","Tel:"+tel);
						
							infos.add(info);
					}
					}
					if(mSearchText.getText().toString().length()>0){
						tvcount.setText("共找到“"+mSearchText.getText().toString()+"”相关的"+infos.size()+"条相关信息");
					}else{
						tvcount.setText("随机找到附近"+infos.size()+"条相关信息");
					}
					
					adapter=new MyAdapter(ShowMap.this, infos);
					list.setAdapter(adapter);
					
					slid.setVisibility(View.VISIBLE);
					slid.open();
					List<SuggestionCity> suggestionCities = poiResult
							.getSearchSuggestionCitys();// 当搜索不到poiitem数据时，会返回含有搜索关键字的城市信息
					if (poiItems != null && poiItems.size() > 0) {
						//清除POI信息显示
						whetherToShowDetailInfo(false);
						//并还原点击marker样式
						if (mlastMarker != null) {
							resetlastmarker();
						}				
						//清理之前搜索结果的marker
						if (poiOverlay !=null) {
							poiOverlay.removeFromMap();
						}
						aMap.clear();
						poiOverlay = new myPoiOverlay(aMap, poiItems);
						poiOverlay.addToMap();
						poiOverlay.zoomToSpan();
//						aMap.addMarker(new MarkerOptions()
//						.anchor(0.5f, 0.5f)
//						.icon(BitmapDescriptorFactory
//								.fromBitmap(BitmapFactory.decodeResource(
//										getResources(), R.drawable.location_marker)))
//						.position(new LatLng(lp.getLatitude(), lp.getLongitude())));
//						
					
					} else if (suggestionCities != null
							&& suggestionCities.size() > 0) {
						showSuggestCity(suggestionCities);
					} else {
						ToastUtil.show(ShowMap.this,
								"没有值");
					}
				}
			} else {
				ToastUtil
						.show(ShowMap.this,"没有值");
			}
		}
	}

	
	/**
	 * 开始进行poi搜索
	 */
	protected void doSearchQuery() {
		keyWord = mSearchText.getText().toString().trim();
	
		currentPage = 0;
		query = new PoiSearch.Query(keyWord, "", "");// 第一个参数表示搜索字符串，第二个参数表示poi搜索类型，第三个参数表示poi搜索区域（空字符串代表全国）
		query.setPageSize(10);// 设置每页最多返回多少条poiitem
		query.setPageNum(currentPage);// 设置查第一页

		if (lp != null) {
			poiSearch = new PoiSearch(this, query);
			poiSearch.setOnPoiSearchListener(this);
			poiSearch.setBound(new SearchBound(lp, 5000, true));//
			// 设置搜索区域为以lp点为圆心，其周围5000米范围
			poiSearch.searchPOIAsyn();// 异步搜索
		}
	}
	
	private void setup() {
		mPoiDetail = (RelativeLayout) findViewById(R.id.poi_detail);
		mPoiDetail.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
//				Intent intent = new Intent(PoiSearchActivity.this,
//						SearchDetailActivity.class);
//				intent.putExtra("poiitem", mPoi);
//				startActivity(intent);
				
			}
		});
		mPoiName = (TextView) findViewById(R.id.poi_name);
		mPoiAddress = (TextView) findViewById(R.id.poi_address);
		mSearchText = (EditText)findViewById(R.id.input_edittext);
		mSearchText.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if(count==0){
					slid.setVisibility(View.GONE);
					if (poiOverlay !=null) {
						poiOverlay.removeFromMap();
					}
					mPoiDetail.setVisibility(View.GONE);
				}
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO 自动生成的方法存根
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				// TODO 自动生成的方法存根
				
			}
		});
	}
	
	
	
	@Override
	public boolean onMarkerClick(Marker marker) {
		slid.setVisibility(View.GONE);
		if (marker.getObject() != null) {
			whetherToShowDetailInfo(true);
			try {
				PoiItem mCurrentPoi = (PoiItem) marker.getObject();
				if (mlastMarker == null) {
					mlastMarker = marker;
				} else {
					// 将之前被点击的marker置为原来的状态
					resetlastmarker();
					mlastMarker = marker;
				}
				detailMarker = marker;
				detailMarker.setIcon(BitmapDescriptorFactory
									.fromBitmap(BitmapFactory.decodeResource(
											getResources(),
											R.drawable.poi_marker_pressed)));

				setPoiItemDisplayContent(mCurrentPoi);
			} catch (Exception e) {
				// TODO: handle exception
			}
		}else {
			whetherToShowDetailInfo(false);
			resetlastmarker();
		}


		return true;
	}
	
	private void resetlastmarker() {
		int index = poiOverlay.getPoiIndex(mlastMarker);
		if (index < 10) {
			mlastMarker.setIcon(BitmapDescriptorFactory
					.fromBitmap(BitmapFactory.decodeResource(
							getResources(),
							markers[index])));
		}else {
			mlastMarker.setIcon(BitmapDescriptorFactory.fromBitmap(
			BitmapFactory.decodeResource(getResources(), R.drawable.marker_other_highlight)));
		}
		mlastMarker = null;
		
	}

	private void setPoiItemDisplayContent(final PoiItem mCurrentPoi) {
		mPoiName.setText(mCurrentPoi.getTitle());
		mPoiAddress.setText(mCurrentPoi.getSnippet());
	}
	/**
	 * poi没有搜索到数据，返回一些推荐城市的信息
	 */
	private void showSuggestCity(List<SuggestionCity> cities) {
		String infomation = "推荐城市\n";
		for (int i = 0; i < cities.size(); i++) {
			infomation += "城市名称:" + cities.get(i).getCityName() + "城市区号:"
					+ cities.get(i).getCityCode() + "城市编码:"
					+ cities.get(i).getAdCode() + "\n";
		}
		ToastUtil.show(this, infomation);

	}
	@Override
	public void onMapClick(LatLng arg0) {
		whetherToShowDetailInfo(false);
		
		if (mlastMarker != null) {
			resetlastmarker();
		}
	}

	private int[] markers = {R.drawable.poi_marker_1,
			R.drawable.poi_marker_2,
			R.drawable.poi_marker_3,
			R.drawable.poi_marker_4,
			R.drawable.poi_marker_5,
			R.drawable.poi_marker_6,
			R.drawable.poi_marker_7,
			R.drawable.poi_marker_8,
			R.drawable.poi_marker_9,
			R.drawable.poi_marker_10
			};
	private class myPoiOverlay {
		private AMap mamap;
		private List<PoiItem> mPois;
	    private ArrayList<Marker> mPoiMarks = new ArrayList<Marker>();
		public myPoiOverlay(AMap amap ,List<PoiItem> pois) {
			mamap = amap;
	        mPois = pois;
		}

	    /**
	     * 添加Marker到地图中。
	     * @since V2.1.0
	     */
	    public void addToMap() {
	        for (int i = 0; i < mPois.size(); i++) {
	            Marker marker = mamap.addMarker(getMarkerOptions(i));
	            PoiItem item = mPois.get(i);
				marker.setObject(item);
	            mPoiMarks.add(marker);
	        }
	    }

	    /**
	     * 去掉PoiOverlay上所有的Marker。
	     *
	     * @since V2.1.0
	     */
	    public void removeFromMap() {
	        for (Marker mark : mPoiMarks) {
	            mark.remove();
	        }
	    }

	    /**
	     * 移动镜头到当前的视角。
	     * @since V2.1.0
	     */
	    public void zoomToSpan() {
	        if (mPois != null && mPois.size() > 0) {
	            if (mamap == null)
	                return;
	            LatLngBounds bounds = getLatLngBounds();
	            mamap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));
	        }
	    }

	    private LatLngBounds getLatLngBounds() {
	        LatLngBounds.Builder b = LatLngBounds.builder();
	        for (int i = 0; i < mPois.size(); i++) {
	            b.include(new LatLng(mPois.get(i).getLatLonPoint().getLatitude(),
	                    mPois.get(i).getLatLonPoint().getLongitude()));
	        }
	        return b.build();
	    }

	    private MarkerOptions getMarkerOptions(int index) {
	        return new MarkerOptions()
	                .position(
	                        new LatLng(mPois.get(index).getLatLonPoint()
	                                .getLatitude(), mPois.get(index)
	                                .getLatLonPoint().getLongitude()))
	                .title(getTitle(index)).snippet(getSnippet(index))
	                .icon(getBitmapDescriptor(index));
	    }

	    protected String getTitle(int index) {
	        return mPois.get(index).getTitle();
	    }

	    protected String getSnippet(int index) {
	        return mPois.get(index).getSnippet();
	    }

	    /**
	     * 从marker中得到poi在list的位置。
	     *
	     * @param marker 一个标记的对象。
	     * @return 返回该marker对应的poi在list的位置。
	     * @since V2.1.0
	     */
	    public int getPoiIndex(Marker marker) {
	        for (int i = 0; i < mPoiMarks.size(); i++) {
	            if (mPoiMarks.get(i).equals(marker)) {
	                return i;
	            }
	        }
	        return -1;
	    }

	    /**
	     * 返回第index的poi的信息。
	     * @param index 第几个poi。
	     * @return poi的信息。poi对象详见搜索服务模块的基础核心包（com.amap.api.services.core）中的类 <strong><a href="../../../../../../Search/com/amap/api/services/core/PoiItem.html" title="com.amap.api.services.core中的类">PoiItem</a></strong>。
	     * @since V2.1.0
	     */
	    public PoiItem getPoiItem(int index) {
	        if (index < 0 || index >= mPois.size()) {
	            return null;
	        }
	        return mPois.get(index);
	    }

		protected BitmapDescriptor getBitmapDescriptor(int arg0) {		
			if (arg0 < 10) {
				BitmapDescriptor icon = BitmapDescriptorFactory.fromBitmap(
						BitmapFactory.decodeResource(getResources(), markers[arg0]));
				return icon;
			}else {
				BitmapDescriptor icon = BitmapDescriptorFactory.fromBitmap(
						BitmapFactory.decodeResource(getResources(), R.drawable.marker_other_highlight));
				return icon;
			}	
		}
	}
	@Override
	public void onPoiItemSearched(PoiItem arg0, int arg1) {
		// TODO 自动生成的方法存根
		
	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_search:
			doSearchQuery();
			break;

		default:
			break;
		}
		
	}



	@Override
	public View getInfoContents(Marker arg0) {
		// TODO 自动生成的方法存根
		return null;
	}



	@Override
	public View getInfoWindow(Marker arg0) {
		// TODO 自动生成的方法存根
		return null;
	}



	@Override
	public void onInfoWindowClick(Marker arg0) {
		// TODO 自动生成的方法存根
		
	}
	private int dp2px(int dp) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
				getResources().getDisplayMetrics());
	}

}
