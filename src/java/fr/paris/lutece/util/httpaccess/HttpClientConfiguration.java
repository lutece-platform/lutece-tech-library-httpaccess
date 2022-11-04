package fr.paris.lutece.util.httpaccess;


/**
 * The Class HttpClientConfiguration.
 */
public class HttpClientConfiguration {


    /** The _str proxy host. */
    private String _strProxyHost;

    /** The _str proxy port. */
    private String _strProxyPort;

    /** The _str proxy user name. */
    private String _strProxyUserName;

    /** The _str proxy password. */
    private String _strProxyPassword;

    /** The _str host name. */
    private String _strHostName;

    /** The _str domain name. */
    private String _strDomainName;

    /** The _str realm. */
    private String _strRealm;

    /** The _str no proxy for. */
    private String _strNoProxyFor;

    /** The _str content charset. */
    private String _strContentCharset;

    /** The _str element charset. */
    private String _strElementCharset;

    /** The _str socket timeout. */
    private Integer _nSocketTimeout;

    /** The _str connection timeout. */
    private Integer _nConnectionTimeout;

    /** The _b connection pool enabled. */
    private boolean _bConnectionPoolEnabled;

    /** The _str connection pool max total connection. */
    private Integer _nConnectionPoolMaxTotalConnection;

    /** The _str connection pool max connection per host. */
    private Integer _nConnectionPoolMaxConnectionPerHost;

	/**
	 * Gets the proxy host.
	 *
	 * @return the proxy host
	 */
	public String getProxyHost() {
		return _strProxyHost;
	}

	/**
	 * Sets the proxy host.
	 *
	 * @param _strProxyHost the new proxy host
	 */
	public void setProxyHost(String _strProxyHost) {
		this._strProxyHost = _strProxyHost;
	}

	/**
	 * Gets the proxy port.// TODO: Auto-generated Javadoc
	 *
	 * @return the proxy port
	 */
	public String getProxyPort() {
		return _strProxyPort;
	}

	/**
	 * Sets the proxy port.
	 *
	 * @param _strProxyPort the new proxy port
	 */
	public void setProxyPort(String _strProxyPort) {
		this._strProxyPort = _strProxyPort;
	}

	/**
	 * Gets the proxy user name.
	 *
	 * @return the proxy user name
	 */
	public String getProxyUserName() {
		return _strProxyUserName;
	}

	/**
	 * Sets the proxy user name.
	 *
	 * @param _strProxyUserName the new proxy user name
	 */
	public void setProxyUserName(String _strProxyUserName) {
		this._strProxyUserName = _strProxyUserName;
	}

	/**
	 * Gets the proxy password.
	 *
	 * @return the proxy password
	 */
	public String getProxyPassword() {
		return _strProxyPassword;// TODO: Auto-generated Javadoc
	}

	/**
	 * Sets the proxy password.
	 *
	 * @param _strProxyPassword the new proxy password
	 */
	public void setProxyPassword(String _strProxyPassword) {
		this._strProxyPassword = _strProxyPassword;
	}

	/**
	 * Gets the host name.
	 *
	 * @return the host name
	 */
	public String getHostName() {
		return _strHostName;
	}

	/**
	 * Sets the host name.
	 *
	 * @param _strHostName the new host name
	 */
	public void setHostName(String _strHostName) {
		this._strHostName = _strHostName;
	}

	/**
	 * Gets the domain name.
	 *
	 * @return the domain name
	 */
	public String getDomainName() {
		return _strDomainName;
	}

	/**
	 * Sets the domain name.
	 *
	 * @param _strDomainName the new domain name
	 */
	public void setDomainName(String _strDomainName) {
		this._strDomainName = _strDomainName;
	}

	/**
	 * Gets the realm.
	 *
	 * @return the realm
	 */
	public String getRealm() {
		return _strRealm;
	}

	/**
	 * Sets the realm.
	 *
	 * @param _strRealm the new realm
	 */
	public void setRealm(String _strRealm) {
		this._strRealm = _strRealm;
	}

	/**
	 * Gets the no proxy for.
	 *
	 * @return the no proxy for
	 */
	public String getNoProxyFor() {
		return _strNoProxyFor;
	}

	/**
	 * Sets the no proxy for.
	 *
	 * @param _strNoProxyFor the new no proxy for
	 */
	public void setNoProxyFor(String _strNoProxyFor) {
		this._strNoProxyFor = _strNoProxyFor;
	}

	/**
	 * Gets the content charset.
	 *
	 * @return the content charset
	 */
	public String getContentCharset() {
		return _strContentCharset;
	}

	/**
	 * Sets the content charset.
	 *
	 * @param _strContentCharset the new content charset
	 */
	public void setContentCharset(String _strContentCharset) {
		this._strContentCharset = _strContentCharset;
	}

	/**
	 * Gets the element charset.
	 *
	 * @return the element charset
	 */
	public String getElementCharset() {
		return _strElementCharset;
	}

	/**
	 * Sets the element charset.
	 *
	 * @param _strElementCharset the new element charset
	 */
	public void setElementCharset(String _strElementCharset) {
		this._strElementCharset = _strElementCharset;
	}

	/**
	 * Gets the socket timeout.
	 *
	 * @return the socket timeout
	 */
	public Integer getSocketTimeout() {
		return _nSocketTimeout;
	}

	/**
	 * Sets the socket timeout.
	 *
	 * @param nSocketTimeout the new socket timeout
	 */
	public void setSocketTimeout(Integer nSocketTimeout) {
		this._nSocketTimeout = nSocketTimeout;
	}

	/**
	 * Gets the connection timeout.
	 *
	 * @return the connection timeout
	 */
	public Integer getConnectionTimeout() {
		return _nConnectionTimeout;
	}

	/**
	 * Sets the connection timeout.
	 *
	 * @param nConnectionTimeout the new connection timeout
	 */
	public void setConnectionTimeout(Integer nConnectionTimeout) {
		this._nConnectionTimeout = nConnectionTimeout;
	}

	/**
	 * Checks if is connection pool enabled.
	 *
	 * @return true, if is connection pool enabled
	 */
	public boolean isConnectionPoolEnabled() {
		return _bConnectionPoolEnabled;
	}

	/**
	 * Sets the connection pool enabled.
	 *
	 * @param _bConnectionPoolEnabled the new connection pool enabled
	 */
	public void setConnectionPoolEnabled(boolean _bConnectionPoolEnabled) {
		this._bConnectionPoolEnabled = _bConnectionPoolEnabled;
	}

	/**
	 * Gets the connection pool max total connection.
	 *
	 * @return the connection pool max total connection
	 */
	public Integer getConnectionPoolMaxTotalConnection() {
		return _nConnectionPoolMaxTotalConnection;
	}

	/**
	 * Sets the connection pool max total connection.
	 *
	 * @param nConnectionPoolMaxTotalConnection the new connection pool max total connection
	 */
	public void setConnectionPoolMaxTotalConnection(Integer nConnectionPoolMaxTotalConnection) {
		this._nConnectionPoolMaxTotalConnection = nConnectionPoolMaxTotalConnection;
	}

	/**
	 * Gets the connection pool max connection per host.
	 *
	 * @return the connection pool max connection per host
	 */
	public Integer getConnectionPoolMaxConnectionPerHost() {
		return _nConnectionPoolMaxConnectionPerHost;
	}

	/**
	 * Sets the connection pool max connection per host.
	 *
	 * @param nConnectionPoolMaxConnectionPerHost the new connection pool max connection per host
	 */
	public void setConnectionPoolMaxConnectionPerHost(Integer nConnectionPoolMaxConnectionPerHost) {
		this._nConnectionPoolMaxConnectionPerHost = nConnectionPoolMaxConnectionPerHost;
	}

}
