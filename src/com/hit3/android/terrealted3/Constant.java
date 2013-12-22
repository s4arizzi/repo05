package com.hit3.android.terrealted3;

public class Constant {
	
	public static String APP_BASE_PATH = "/data/data/com.hit3.android.terrealted3/";
	
	public static String DB_NAME = "terrealte_v1.sqlite";
	public static String DB_DUMP_ARCHIVE = "terrealteDB.zip";
	
	public static final String AUTHORITY = "com.hit3.android.terrealte.provider";

	//public static final String DEFAULT_IP = "10.112.0.172/terrealted7";
	public static final String WEB_SERVER = "http://terrealte.stage.s4win.com";
	public static final String WEB_SERVER_ENDPOINT = "/rest";
	public static final String WEB_THUMBNAIL_IMAGE = "/sites/default/files/danland_logo.jpg";
	
	public static final int THUMBNAIL_IMAGE = R.drawable.logo;
	
	/*
	public static final String SERVICE_00 = "/?q=node_services/node/[?].json";	
	public static final String SERVICE_10 = "/?q=web_services_list_rivista";
	public static final String SERVICE_11 = "/?q=web_services_list_rivista&pages=[?]";
	public static final String SERVICE_12 = "/web_services_sync_rivista";	
	public static final String SERVICE_20 = "/?q=web_services_list_libro";
	public static final String SERVICE_21 = "/?q=web_services_list_libro&pages=[?]";
	public static final String SERVICE_22 = "/web_services_sync_libro";
	public static final String SERVICE_30 = "/?q=web_services_list_cartina";
	public static final String SERVICE_31 = "/?q=web_services_list_cartina&pages=[?]";
	public static final String SERVICE_32 = "/web_services_sync_cartina";
	public static final String SERVICE_40 = "/?q=web_services_list_pdf";
	public static final String SERVICE_41 = "/?q=web_services_list_pdf&pages=[?]";
	public static final String SERVICE_50 = "/?q=web_services_list_rifugio";
	public static final String SERVICE_51 = "/?q=web_services_list_rifugio&pages=[?]";
	public static final String SERVICE_52 = "/web_services_sync_rifugio";
	public static final String SERVICE_60 = "/?q=web_services_list_materiale";
	public static final String SERVICE_61 = "/?q=web_services_list_materiale&pages=[?]";
	public static final String SERVICE_70 = "/?q=web_services_list_canto";
	public static final String SERVICE_71 = "/?q=web_services_list_canto&pages=[?]";
	public static final String SERVICE_72 = "/web_services_sync_canto";
	public static final String SERVICE_82 = "/web_services_sync_fiore";
	*/
	
	public static final String SERVICE_11 = "/web_services_magazines";	
	public static final String SERVICE_12 = "/web_services_magazines_full";	
	public static final String SERVICE_13 = "/web_services_magazines_library_filter";	
	public static final String SERVICE_21 = "/web_services_books";
	public static final String SERVICE_22 = "/web_services_books_full";
	public static final String SERVICE_23 = "/web_services_books_library_filter";
	public static final String SERVICE_31 = "/web_services_maps";
	public static final String SERVICE_32 = "/web_services_maps_full";
	public static final String SERVICE_33 = "/web_services_maps_scale_filter";
	public static final String SERVICE_51 = "/web_services_refuges";
	public static final String SERVICE_52 = "/web_services_refuges_full";
	public static final String SERVICE_71 = "/web_services_songs";
	public static final String SERVICE_72 = "/web_services_songs_full";
	public static final String SERVICE_73 = "/web_services_songs_category_filter";
	public static final String SERVICE_81 = "/web_services_flowers";
	public static final String SERVICE_82 = "/web_services_flowers_full";
	public static final String SERVICE_83 = "/web_services_flowers_color_filter";
	public static final String SERVICE_91 = "/web_services_animals";
	public static final String SERVICE_92 = "/web_services_animals_full";
	public static final String SERVICE_93 = "/web_services_animals_species_filter";
	
	public static final String CACHE_IMAGE_TYPE_NORMAL = "normal";
	public static final String CACHE_IMAGE_TYPE_THUMBNAIL = "thumbnail";
	public static final String CACHE_IMAGE_TYPE_MEDIUM = "medium";
	public static final String CACHE_IMAGE_TYPE_LARGE = "large";
	public static final String CACHE_IMAGE_TYPE_CUSTOM = "custom";
	
	
	public static enum CONTENT_TYPE {MAGAZINES, BOOKS, MAPS, SONGS, REFUGES, FLOWERS, ANIMALS};
	
	public static final String EXTRAS_NID = "nid";
	//public static final String EXTRAS_WEB_SERVER = "web_server";
	//public static final String EXTRAS_BIBLIOTECA = "biblioteca";
	//public static final String EXTRAS_IMAGE_URL = "image_url";
	
	public static final int ACTIVITY_SHOW_LIST_VIEW = 10;
	public static final int ACTIVITY_MENU = 11;
	public static final int ACTIVITY_SHOW_IMAGE = 12;
	public static final int ACTIVITY_SETTING = 13;
	public static final int ACTIVITY_SYNCRONIZE = 14;
	public static final int ACTIVITY_WEBVIEW = 15;
	public static final int ACTIVITY_INFO = 16;
	
	public static final String SEPARATOR = "|";
	
	
	public static final String TAXONOMY_SCALA_TOPOGRAFICA_41 = "1:25000";
	public static final String TAXONOMY_SCALA_TOPOGRAFICA_42 = "1:35000";
	public static final String TAXONOMY_SCALA_TOPOGRAFICA_43 = "1:50000";
	
	public static final String BUNDLE_FRAGMENT_TAG = "BUNDLE_FRAGMENT_TAG";
	public static final String BUNDLE_ITEM_INDEX = "BUNDLE_ITEM_INDEX"; //deprecated
	public static final String BUNDLE_ITEM_ID_TITLE = "BUNDLE_ITEM_ID_TITLE";
	public static final String BUNDLE_WEB_SERVICE = "BUNDLE_WEB_SERVICE";
	public static final String BUNDLE_ROW_LAYOUT = "BUNDLE_ROW_LAYOUT";
	public static final String BUNDLE_NID = "BUNDLE_NID";
	//public static final String BUNDLE_TYPE = "BUNDLE_TYPE";
	public static final String BUNDLE_IMAGE_NAME = "BUNDLE_IMAGE_NAME";
	public static final String BUNDLE_EXTRA = "BUNDLE_EXTRA";
	public static final String BUNDLE_ITEM_REFUGES_DISTANCE = "BUNDLE_ITEM_REFUGES_DISTANCE";
	
	public static final String EXTRAS_WEBVIEW = "html_file_name";
	
	
	public static final String INSERT_DATA = "INSERT DATA";
	public static final String UPDATE_DATA = "UPDATE DATA";
	public static final String DELETE_DATA = "DELETE DATA";
	public static final String UPDATE_CACHE = "UPDATE CACHE";
	public static final String DELETE_CACHE = "DELETE CACHE";
	
	
	
	public static final String LOGIN_USERNAME = "username";
	public static final String LOGIN_PASSWORD = "password";
	public static final String LOGIN_UID = "uid";
	public static final String LOGIN_ROLES = "roles";
	public static final String LOGIN_CREATED = "created";
	public static final String LOGIN_ACCESS = "access";
	public static final String LOGIN_TIMEZONE = "timezone";
	public static final String LOGIN_LANGUAGE = "language";
	public static final String LOGIN_COOKIE = "coolie";
	public static final String LOGIN_STATUS_CODE = "status_code";
	public static final String LOGIN_LOGIN = "login";
	
	public static final Integer AUTH_ALL = 0;
	public static final Integer AUTH_AUTHENTICATED_USER = 2;
	public static final Integer AUTH_ADMINISTRATOR = 3;
	public static final Integer AUTH_UTENTE_AREA_RISERVATA = 5;
	
	
}
