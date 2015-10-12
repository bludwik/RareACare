package com.blsoft.rareacare.client;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.blsoft.rareacare.client.place.HelloPlace;
import com.blsoft.rareacare.client.place.LoginPlace;
import com.blsoft.rareacare.client.requestfactory.RegistryProxy;
import com.blsoft.rareacare.client.requestfactory.UserDataRequest;
import com.blsoft.rareacare.client.requestfactory.UserProxy;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsArrayString;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.web.bindery.requestfactory.shared.EntityProxy;
import com.google.web.bindery.requestfactory.shared.Receiver;
import com.google.web.bindery.requestfactory.shared.ServerFailure;
import com.googlecode.gwt.crypto.bouncycastle.digests.SHA1Digest;

/** Klasa narzędziowa z różnymi funkcjami dostępnymi po stronie kienta
 * 
 * @author bartek */
public class Utils {
	private static IClientFactory iClientFactory;
	private static UserProxy user = null;
	private static Map<Integer, RegistryProxy> regDefsCache = new HashMap<Integer, RegistryProxy>();

	public interface HtmlTemplates extends SafeHtmlTemplates {
		@Template("    <div class = \"gwt-CellListItem\"><table border=\"0\" width=\"100%\">\n" +
				"      <tbody>\n" +
				"        <tr>\n" +
				"          <td style=\"font-weight: bold; width:8EM;\">{0}</td>\n" +
				"          <td style=\"font-weight: bold;\">{1}</td>\n" +
				"        </tr>\n" +
				"        <tr>\n" +
				"          <td colspan=\"2\" style=\"font-size:95%;\">{2}</td>\n" +
				"        </tr>\n" +
				"      </tbody>\n" +
				"    </table></div>")
		public SafeHtml regEditNewDocItem(String id, String name, String comment);
	}

	public static final HtmlTemplates TEMPLATES = GWT.create(HtmlTemplates.class);

	static public IClientFactory getCF() {
		return iClientFactory;
	}

	static public void setCF(IClientFactory cf) {
		iClientFactory = cf;
	}

	static public String ChkNumeric(String v, String field) {
		if (v == null || v.trim().isEmpty())
			return null;
		try {
			Double.valueOf(v);
		} catch (Exception e) {
			if (field != null)
				getCF().showError("Podana wartość: " + v + " nie jest prawidłową wartością liczbową dla pola: " + field);
			else
				getCF().showError("Podana wartość: " + v + " nie jest prawidłową wartością liczbową");
		}
		return v;
	}

	static public String ChkInteger(String v, String field) {
		if (v == null || v.trim().isEmpty())
			return null;
		try {
			Integer.valueOf(v);
		} catch (Exception e) {
			if (field != null)
				getCF().showError("Podana wartość: " + v + " nie jest prawidłową wartością całkowitą dla pola: " + field);
			else
				getCF().showError("Podana wartość: " + v + " nie jest prawidłową wartością całkowitą");
		}
		return v;
	}

	static public String ChkNotEmpty(String v, String field) {
		if (v == null || v.trim().isEmpty())
			if (field != null)
				getCF().showError("Nie wypeniono wymaganego pola");
			else
				getCF().showError("Nie wypeniono wymaganego pola: " + field);
		return v;
	}

	/** Odnajduje na liście Entity o tym samym ID i je zamienia; Jeśli nie
	 * znajdzie, to dodaje
	 * 
	 * @param list
	 *            - lista
	 * @param ent
	 *            - entity do dodania
	 * @return - poprzednie entity */
	@SuppressWarnings("unchecked")
	public static <T extends EntityProxy> T replaceOrAddEntity(List<T> list, T ent) {
		if (ent == null)
			return null;
		for (EntityProxy e : list) {
			if (e.stableId().equals(ent.stableId())) {
				int i = list.indexOf(e);
				list.set(i, ent);
				return (T) e;
			}
		}
		// Nie znalazł - dodaję do listy
		list.add(ent);
		return null;
	}

	public static <T extends EntityProxy> boolean removeEntity(List<T> list, T ent) {
		if (ent == null)
			return false;
		for (EntityProxy e : list) {
			if (e.stableId().equals(ent.stableId())) {
				int i = list.indexOf(e);
				list.remove(i);
				return true;
			}
		}
		return false;
	}

	private static final char[] CHARS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz".toCharArray();

	/** Generate a random uuid of the specified length. Example: uuid(15) returns
	 * "VcydxgltxrVZSTV"
	 * 
	 * @param len
	 *            the desired number of characters */
	public static String uuid(int len) {
		return uuid(len, CHARS.length);
	}

	/** Generate a random uuid of the specified length, and radix. Examples:
	 * <ul>
	 * <li>uuid(8, 2) returns "01001010" (8 character ID, base=2)
	 * <li>uuid(8, 10) returns "47473046" (8 character ID, base=10)
	 * <li>uuid(8, 16) returns "098F4D35" (8 character ID, base=16)
	 * </ul>
	 * 
	 * @param len
	 *            the desired number of characters
	 * @param radix
	 *            the number of allowable values for each character (must be <=
	 *            62) */
	public static String uuid(int len, int radix) {
		if (radix > CHARS.length) {
			throw new IllegalArgumentException();
		}
		char[] uuid = new char[len];
		// Compact form
		for (int i = 0; i < len; i++) {
			uuid[i] = CHARS[(int) (Math.random() * radix)];
		}
		return new String(uuid);
	}

	/** Generate a RFC4122, version 4 ID. Example:
	 * "92329D39-6F5C-4520-ABFC-AAB64544E172" */
	public static String uuid() {
		char[] uuid = new char[36];
		int r;

		// rfc4122 requires these characters
		uuid[8] = uuid[13] = uuid[18] = uuid[23] = '-';
		uuid[14] = '4';

		// Fill in random data. At i==19 set the high bits of clock sequence as
		// per rfc4122, sec. 4.1.5
		for (int i = 0; i < 36; i++) {
			if (uuid[i] == 0) {
				r = (int) (Math.random() * 16);
				uuid[i] = CHARS[(i == 19) ? (r & 0x3) | 0x8 : r & 0xf];
			}
		}
		return new String(uuid);
	}

	/** Wylogowuje użytkownika */
	public static void logout(final boolean gotoMain) {
		UserDataRequest req = iClientFactory.getRequestFactory().userDataRequest();
		setUser(null);
		req.logOut().fire();
		getCF().goTo(new HelloPlace());
	}

	public static String format(final String format, final Object... args) {
		if (null == args || 0 == args.length)
			return format;
		JsArrayString array = newArray();
		for (Object arg : args) {
			array.push(String.valueOf(arg)); // TODO: smarter conversion?
		}
		return nativeFormat(format, array);
	}

	private static native JsArrayString newArray()/*-{
		return [];
	}-*/;

	private static native String nativeFormat(final String format, final JsArrayString args)/*-{
		return format.replace(/{(\d+)}/g, function(match, number) {
			return typeof args[number] != 'undefined' ? args[number] : match;
		});
	}-*/;

	public static boolean isEmpty(Object val) {
		return (val != null && !val.toString().isEmpty());
	}

	public static boolean isEmpty(String val) {
		return (val == null || val.isEmpty());
	}

	public static String joinItems(String id, String id2) {
		if (!isEmpty(id) && !isEmpty(id2))
			return id + "/" + id2;
		else if (!isEmpty(id2))
			return id2;
		else
			return id;
	}

	public static String coalesce(String s) {
		if (s == null)
			return "";
		else
			return s;
	}

	/** Funkcja konwertujaca string na bezpieczny string dla HTMLa (w tym NULL->
	 * "") */
	public static String safe(String s) {
		s = coalesce(s);
		SafeHtmlBuilder sb = new SafeHtmlBuilder();
		return sb.appendEscaped(s).toSafeHtml().asString();
	}

	public static String escapeHtmlNl(String s) {
		return escapeHTML(s).replaceAll("\n", "<br>");
	}

		public static String escapeHTML(String s) {
		if (s == null || s == "")
			return "";
		else {
			StringBuilder out = new StringBuilder(Math.max(16, s.length()));
			for (int i = 0; i < s.length(); i++) {
				char c = s.charAt(i);
				if (c > 127 || c == '"' || c == '<' || c == '>' || c == '&') {
					out.append("&#");
					out.append((int) c);
					out.append(';');
				} else {
					out.append(c);
				}
			}
			return out.toString();
		}
	}

	public static String coalesce(String s, String repl) {
		if (s == null)
			return repl;
		else
			return s;
	}

	/** Aktualnie zalogowany użytkownik (ID) */
	public static int getUserId() {
		if (user == null)
			return 0;
		return user.getId();
	}

	/** Aktualnie zalogowany użytkownik */
	public static UserProxy getUser() {
		return user;
	}

	/** Usrtawia aktualnego użytkownika
	 * 
	 * @param user
	 *            the user to set */
	public static void setUser(UserProxy user) {
		Utils.user = user;
	}

	public static boolean isLoggedIn() {
		return (user != null);
	}

	/** Funkcja sprawdza, czy user jest zalogowany. jeśli nie, to próbuje
	 * pobrać usera z sesji; Jeśli sie to nie powiedzie, to przekierowuje
	 * aplikację do strony logowania */
	public static void CheckLogIn() {
		if (getUserId() <= 0) {
			UserDataRequest req = iClientFactory.getRequestFactory().userDataRequest();
			req.getLoggedUser().fire(new Receiver<UserProxy>() {

				@Override
				public void onSuccess(UserProxy response) {
					user = response;
					if (user == null) {
						getCF().showMessage("Przed kontynuowaniem musisz się zalogować");
						getCF().goTo(new LoginPlace());
					}
				}

				@Override
				public void onFailure(ServerFailure error) {
					if (error.isFatal()) {
						throw new RuntimeException(error.getMessage());
					}
					getCF().showMessage("Przed kontynuowaniem musisz się zalogować");
					getCF().goTo(new LoginPlace());
				}
			});
		}
	}

	public static RegistryProxy getRegDefFromCache(Integer rapId) {
		RegistryProxy reg = regDefsCache.get(rapId);

		// Jeśli do rejestru podpiętu jest inny user, niż zalogowany, to wyczyść
		// cache
		if (reg != null && (!isLoggedIn() || reg.getCurrRegUser() == null || reg.getCurrRegUser().getUser() == null || reg.getCurrRegUser().getUser().getId() != getUserId())) {
			regDefsCache.clear();
			return null;
		}
		
//		if (reg != null) {
//			UserProxy us = reg.getCurrRegUser().getUser();
//		}
		
		return reg;
	}

	public static void setRegDefToCache(Integer rapId, RegistryProxy def) {
		if (def == null)
			regDefsCache.remove(rapId);
		else
			regDefsCache.put(rapId, def);
	}

	public static boolean chkIdRules(String value, boolean b) {
		if (value.indexOf('/') < 0 && value.indexOf('.') < 0 && value.indexOf(' ') < 0 && value.indexOf('|') < 0 && value.indexOf('(') < 0 && value
				.indexOf(')') < 0)
			return true;
		if (b)
			Utils.getCF().showMessage("Identyfikator nie może zawierać spacji, znaku (, ), \"/\", \"|\", ani \".\"");
		return false;
	}

	private static final String allowedIdChars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789_-:";

	public static String wrapId(String value) {
		String res = "";
		char ch;
		for (int i = 0; i < value.length(); i++) {
			ch = value.charAt(i);
			if (allowedIdChars.indexOf(ch) >= 0)
				res += ch;
			else
				res += "\\" + ch;
		}
		return res;
	}

	public static String[] parseFieldPath(String f) {
		String[] ret = { "", "" };
		int p = f.indexOf('|');
		if (p >= 0) {
			ret[1] = f.substring(p + 1);
			ret[0] = f.substring(0, p);
		} else if (p < 0) {
			ret[1] = f;
		} else {
			ret[1] = f.substring(1);
		}
		return ret;
	}

	public static void showMessage(String msg) {
		getCF().showMessage(msg);
	}

	public static boolean isNumeric(String v) {
		try {
			Double.parseDouble(v);
		} catch (NumberFormatException nfe) {
			return false;
		}
		return true;
	}
	
	public static DateTimeFormat dateFormat = DateTimeFormat.getFormat("yyyy-MM-dd");
	public static DateTimeFormat timeFormat = DateTimeFormat.getFormat("hh:mm");
	public static DateTimeFormat dateTimeFormat = DateTimeFormat.getFormat("yyyy-MM-dd hh:mm");
//	public static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
//	public static SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm");
//	public static SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm");

	public static boolean isDate(String v) {
		try {
			dateFormat.parseStrict(v);
		} catch (IllegalArgumentException e) {
			return false;
		}
 		return true;
	}

	public static boolean isTime(String v) {
		try {
			timeFormat.parseStrict(v);
		} catch (IllegalArgumentException e) {
			return false;
		}
 		return true;
	}

	public static boolean isDateTime(String v) {
		try {
			dateTimeFormat.parseStrict(v);
		} catch (IllegalArgumentException e) {
			return false;
		}
 		return true;
	}

	public static void showError(String msg) {
		getCF().showError(msg);
		
	}

	public static boolean confirm(String msg) {
		return getCF().confirm(msg);
	}
	
	public static String getSHA1for(String text) {
		  SHA1Digest sd = new SHA1Digest();
		  byte[] bs = text.getBytes();
		  sd.update(bs, 0, bs.length);
		  byte[] result = new byte[20];
		  sd.doFinal(result, 0);
		  return byteArrayToHexString(result);
		}

	public static String byteArrayToHexString(final byte[] b) {
		  final StringBuffer sb = new StringBuffer(b.length * 2);
		  for (int i = 0, len = b.length; i < len; i++) {
		    int v = b[i] & 0xff;
		    if (v < 16) sb.append('0');
		    sb.append(Integer.toHexString(v));
		  }
		  return sb.toString();
		}

	public static boolean CheckLogInForced() {
		if (user == null) {
			getCF().goTo(new LoginPlace());
			return false;
		}
		return true;
	}	
	
	public static native void updateDateTimePicker() /*-{
		$wnd.jQuery('.datetimepicker').datetimepicker({
			dayOfWeekStart : 1,
			mask:'9999-19-39 29:59',
	        formatTime:'H:i',
	        formatDate:'Y-m-d',
	        format:'Y-m-d H:i'			
		});
		$wnd.jQuery('.datepicker').datetimepicker({
			dayOfWeekStart : 1,
			mask:'9999-19-39',
	        formatDate:'Y-m-d',
	        format:'Y-m-d',			
			timepicker:false,
			defaultTime:'00:00'	        			
		});
		$wnd.jQuery('.timepicker').datetimepicker({
			dayOfWeekStart : 1,
			mask:'29:59',
	        formatTime:'H:i',
	        format:'H:i',			
			datepicker:false	        			
		});
	}-*/;
}
