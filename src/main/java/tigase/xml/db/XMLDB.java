/*
 * Tigase Jabber/XMPP Server
 * Copyright (C) 2004-2014 "Tigase, Inc." <office@tigase.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, version 3 of the License,
 * or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. Look for COPYING file in the top folder.
 * If not, see http://www.gnu.org/licenses/.
 */

package tigase.xml.db;

import tigase.xml.DomBuilderHandler;
import tigase.xml.Element;
import tigase.xml.SimpleParser;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <code>XMLDB</code> is the main database access class.
 * It allows you to create new database in given file, open database from
 * given file, add, delete and retrieve data and data lists. All data or data
 * lists are stored in database nodes. There are three possible kinds of nodes
 * for each database:
 * <ol>
 * <li><b>root node</b> - this is top node in each <em>XML</em> hierachy tree.
 *  There can be only one root node in database as there can be only one root
 *  element in <em>XML</em> file. The element name for root node can be defined
 *  by the user when new database is created or defualt element name
 *  '<code>root</code>' is used.</li>
 * <li><b>node1 nodes</b> - these are the first level nodes under <em>root</em>
 *  node. There can be any number of nodes on this level. All data added for
 *  this database are added to first level node unless subnode path is
 *  given. User can define element name of <em>node1</em> when new database is
 *  created. If not given default element name '<code>node</code>' is used.</li>
 * <li><b>subnodes</b> - node on any deeper level under <em>node1</em>
 *  level. There can be any number of <em>subnodes</em> on any
 *  level. <em>Subnodes</em> have always '<code>node</code>' element name and this
 *  can't be changed.</li>
 * </ol>
 * <p>All <em>node1</em> nodes and <em>subnodes</em> can contains any number of
 * data associated with keys. With some keys there ca be more than one value
 * assigned. Such kind of data are called <em>data lists</em>.<br/>
 * Although element name for <em>subnode</em> can not be defined it is actually not
 * important. Because user database doesn't use subnode element names. He doesn't
 * even use neiher <em>root</em> node element name nor <em>node1</em> element
 * name. database user uses <em><b>node name</b></em> what is quite different
 * from <b><em>node element name</em></b>. Let see example below:</p>
 * <pre>&#60;node name='roster'/&#62;</pre>
 * <p>In this example <em>node element name</em> is <b>node</b> and
 * <em>node name</em> is <b>roster.</b><br/>
 * database users (actually developers) use only <em>node names</em>.<br/>
 * If you want to access subnode on some level you need to give full path to
 * this subnode. For example, let's assume we have following database:</p>
 * <pre>  &#60;node name='tigase'>
 * &#60;node name='server'>
 * &#60;/node>
 * &#60;node name='xmpp'>
 * &#60;/node>
 * &#60;/node></pre>
 * <p>If you need to access '<code>server</code>' subnode you need to call method
 * with '<code>/tigase/server</code>' as subnode path and for subnode
 * '<code>xmpp</code>' proper subnode path is of course
 * '<code>/tigase/xmpp</code>'. If you skip subnode path or give
 * <code>null</code> as a parameter you will be accessing data on <em>node1</em>
 * level. You can not access or save data on root node level.</p>
 *
 * <p>
 * Created: Tue Oct 26 15:27:33 2004
 * </p>
 * @author <a href="mailto:artur.hefczyc@tigase.org">Artur Hefczyc</a>
 */
public class XMLDB {

	/**
	 *
	 */
	private static Logger log = Logger.getLogger("tigase.xml.db.XMLDB");

	//~--- fields ---------------------------------------------------------------

	/** dbFile filename filed*/
	private String dbFile                  = "xml_db.xml";

	/** memoryMode indicates whether XML should be kept only in memory*/
	private boolean memoryMode						 = false;

	/** node_name filed*/
	private String node_name              = "node";

	/** root element filed*/
	private DBElement root                 = null;

	/** root element name filed*/
	private String root_name               = "root";

	/** field indicates if nodes were modified */
	private boolean nodes_modified        = true;

	/** nodes filed*/
	private DBElement[] nodes             = new DBElement[] {};

	/** lock filed*/
	private Lock lock                      = new ReentrantLock();

	/** file saver task */
	private final DBSaver db_saver         = new DBSaver();

	/** DBElementComparator filed*/
	private DBElementComparator comparator = new DBElementComparator();

	/**
	 * Used only for searching for given node, do NOT use for any
	 * other purpose.
	 */
	private DBElement tmp_node = null;

	/**
	 * Creates default XMLDB object, if file modes is enabled then
	 * appropriate saver thread is created as well
	 */
	private XMLDB() {
		if ( !memoryMode ){
			Thread thrd = new Thread( db_saver );

			thrd.setName( "XMLDBSaver" );
			thrd.setDaemon( true );
			thrd.start();
		}
	}

	/**
	 * Creates XMLDB object with desired filename, if file modes is enabled then
	 * appropriate saver thread is created as well. For filenames starting with
	 * "memory://" memory mode (i.e. without writing to disk) is enabled
	 *
	 * @param db_file indicates path to the file on disk to/from which write/read;
	 *                if parameter starts with "memory://" then memory mode
	 *                (without actual file usage) is enabled
	 *
	 * @throws IOException
	 * @throws XMLDBException
	 */
	public XMLDB( String db_file ) throws IOException, XMLDBException {
		dbFile = db_file;
		tmp_node = new DBElement( node_name );
		if ( db_file.startsWith( "memory://" ) ){
			memoryMode = true;
			this.setupNewDB( db_file, root_name, node_name );
		} else {
			Thread thrd = new Thread( db_saver );

			thrd.setName( "XMLDBSaver" + db_file );
			thrd.setDaemon( true );
			thrd.start();

			loadDB();
		}
	}

	/**
	 * Factory method creating and setting up XMLDB
	 *
	 * @param db_file indicates path to the file on disk to/from which write/read;
	 *                if parameter starts with "memory://" then memory mode
	 *                (without actual file usage) is enabled
	 *
	 * @param root_name name of the root element
	 * @param node1_name name of the node
	 * @return XMLDB object
	 */
	public static XMLDB createDB(String db_file, String root_name, String node1_name) {
		XMLDB xmldb = new XMLDB();

		xmldb.setupNewDB(db_file, root_name, node1_name);

		return xmldb;
	}

	/**
	 * Retrieves filename
	 *
	 * @return filename
	 */
	public String getDBFileName() {
		return dbFile;
	}

	@Override
	public String toString() {
		return root != null ? root.formatedString(0, 1) : "";
	}

	/**
	 * Creates basic Elements of the XMLDB
	 *
	 * @param db_file indicates path to the file on disk to/from which write/read;
	 *                if parameter starts with "memory://" then memory mode
	 *                (without actual file usage) is enabled
	 *
	 * @param root_name name of the root element
	 * @param node1_name name of the node
	 */
	protected void setupNewDB(String db_file, String root_name, String node1_name) {
		log.log(Level.FINEST, "Created new XML Database, db_file: {0}, root_name: {1}, node_name: {2} @ {3}",
													new Object [] {db_file, root_name, node1_name, this});
		this.dbFile = db_file;
		if ( db_file.startsWith( "memory://" ) ){
			this.memoryMode = true;
		}
		if (root_name != null) {
			this.root_name = root_name;
		}    // end of if (root_name != null)
		if (node1_name != null) {
			this.node_name = node1_name;
		}    // end of if (node1_name != null)
		tmp_node = new DBElement(node1_name);
		log.log(Level.FINEST, "Created tmp_node1: {0}", new Object [] {tmp_node});
		root      = new DBElement(this.root_name);
		log.log(Level.FINEST, "Created root: {0} @ {1}", new Object [] {root,this.toString()});

	}

	/**
	 * Loads XML from file
	 *
	 * @throws IOException
	 * @throws XMLDBException
	 */
	protected void loadDB() throws IOException, XMLDBException {
		InputStreamReader file       = new InputStreamReader(new FileInputStream(dbFile),
																		 "UTF-8");
		char[] buff                  = new char[16 * 1024];
		SimpleParser parser          = new SimpleParser();
		DomBuilderHandler domHandler = new DomBuilderHandler(DBElementFactory.getFactory());
		int result                   = -1;

		if ( !memoryMode ){
			while ( ( result = file.read( buff ) ) != -1 ) {
				parser.parse( domHandler, buff, 0, result );
			}
			file.close();
		}
		root = (DBElement) domHandler.getParsedElements().poll();

		// node1s = root.getChildren();
		if (root == null) {
			throw new XMLDBException("Invalid XML DB File");
		}
		this.root_name = root.getName();

		List<Element> children = root.getChildren();

		if ((children != null) && (children.size() > 0)) {
			this.node_name = children.get(0).getName();
		}    // end of if (children != null && children.size() > 0)
		log.finest(root.formatedString(0, 2));
	}

	/**
	 * Saves XML to file
	 */
	protected void saveDB() {
		if ( !memoryMode ){
			synchronized ( db_saver ) {
				db_saver.notifyAll();
			}
		}
	}

	/**
	 * Retrieve number of nodes
	 * @return number of nodes
	 */
	public final long getAllNode1sCount() {
		return root.getChildren().size();
	}

	/**
	 * Retrieve list of nodes
	 * @return list of nodes
	 */
	public final List<String> getAllNode1s() {
		List<Element> children = root.getChildren();

		if (children != null) {
			List<String> results = new ArrayList<String>(children.size());

			for (Element child : children) {
				results.add(child.getAttributeStaticStr(DBElement.NAME));
			}    // end of for (Element child: children)

			return results;
		}      // end of if (children != null)

		return null;
	}

	/**
	 * Return Element corresponding to the node name
	 * @param node1_id node name
	 * @return Element corresponding to the node name
	 */
	public final DBElement findNode1(String node1_id) {
		DBElement result = null;

		lock.lock();
		try {
			tmp_node.setAttribute(DBElement.NAME, node1_id);

			int idx        = Arrays.binarySearch(nodes, tmp_node, comparator);
			DBElement dbel = null;

			if (idx >= 0) {
				dbel = nodes[idx];
			}      // end of if (idx >= 0)
			if (nodes_modified && ((idx < 0) || ((dbel != null) && dbel.removed))) {
				List<Element> children = root.getChildren();

				if (children != null) {
					nodes = children.toArray(new DBElement[children.size()]);
					Arrays.sort(nodes, comparator);
					idx = Arrays.binarySearch(nodes, tmp_node, comparator);
				}    // end of if (children != null)
				nodes_modified = false;
			}      // end of if (idx < 0)
			if (idx >= 0) {
				result = nodes[idx];
			}
		} finally {
			lock.unlock();
		}        // end of try-finally

		return result;
	}

	/**
	 *
	 * @param node1_id
	 * @return
	 * @throws NodeNotFoundException
	 */
	protected final DBElement getNode1(String node1_id) throws NodeNotFoundException {
		DBElement result = findNode1(node1_id);

		if (result != null) {
			return result;
		} else {
			throw new NodeNotFoundException("Node1: " + node1_id +
																			" has not been found in db.");
		}    // end of if (result != null) else
	}

	/**
	 * Adds new node
	 *
	 * @param node1_id name of the node to add
	 * @throws NodeExistsException
	 */
	public void addNode1(String node1_id) throws NodeExistsException {
		lock.lock();
		try {
			try {
				getNode1(node1_id);

				throw new NodeExistsException("Node1: " + node1_id + " already exists.");
			} catch (NodeNotFoundException e) {
				nodes_modified = true;

				DBElement newNode1 = new DBElement(node_name, DBElement.NAME, node1_id);

				newNode1.addChild(new DBElement(DBElement.MAP));
				root.addChild(newNode1);
			}    // end of try-catch
		} finally {
			lock.unlock();
		}      // end of try-finally
	}

	/**
	 * Removes the node
	 *
	 * @param node1_id name of the node to remove
	 * @throws NodeNotFoundException
	 */
	public void removeNode1(String node1_id) throws NodeNotFoundException {
		lock.lock();
		try {
			DBElement dbel = getNode1(node1_id);

			nodes_modified = true;
			root.removeChild(dbel);
			dbel.removed = true;
		} finally {
			lock.unlock();
		}    // end of try-finally
		saveDB();
	}

	/**
	 * Retrieves the node of the given name at specific path
	 *
	 * @param node1_id name of the node
	 * @param subnode path to the node
	 * @param auto_create whether to create path if it's missing
	 * @return
	 * @throws NodeNotFoundException
	 */
	protected final DBElement getNode(String node1_id, String subnode, boolean auto_create)
					throws NodeNotFoundException {
		DBElement node1 = getNode1(node1_id);

		log.log( Level.FINEST, "Getting node, node1_id: {0}, subnode: {1}, auto_create: {2}, node1: {3} @ {4}",
						 new Object[] { node1_id, subnode, auto_create, node1, this } );

		if (subnode != null) {
			DBElement node = node1.getSubnodePath(subnode);

			if ((node == null) && auto_create) {
				node = node1.buildNodesTree(subnode);
			}    // end of if (subnode != null)

			return node;
		}

		return node1;
	}

	/**
	 * Sets data for the given node at given path
	 *
	 * @param node1_id name of the node
	 * @param subnode path to the node
	 * @param key under which save the data
	 * @param value actual value to be saved
	 * @throws NodeNotFoundException
	 */
	public void setData(String node1_id, String subnode, String key, Object value)
					throws NodeNotFoundException {
		log.log( Level.FINEST, "Getting node, node1_id: {0}, subnode: {1}, key: {2}, value: {3} @ {4}",
						 new Object[] { node1_id, subnode, key, value, this } );
		getNode(node1_id, subnode, true).setEntry(key, value);
		saveDB();
	}

	/**
	 * Sets data for the given node at root
	 *
	 * @param node1_id name of the node
	 * @param key under which save the data
	 * @param value actual value to be saved
	 * @throws NodeNotFoundException
	 */
	public void setData(String node1_id, String key, Object value)
					throws NodeNotFoundException {
		setData(node1_id, null, key, value);
	}

	/**
	 * Retrieve values of given node under specific path
	 *
	 * @param node1_id name of the node
	 * @param subnode path to the node
	 * @param key under which read the data
	 *
	 * @return array of Strings
	 *
	 * @throws NodeNotFoundException
	 */
	public String[] getDataList(String node1_id, String subnode, String key)
					throws NodeNotFoundException {
		DBElement node = getNode(node1_id, subnode, false);

		return ((node != null)
						? node.getEntryStringArrValue(key, null)
						: null);
	}

	/**
	 * Retrieve values of given node under specific path
	 *
	 * @param node1_id name of the node
	 * @param subnode path to the node
	 * @param key under which read the data
	 *
	 * @return array of Integers
	 *
	 * @throws NodeNotFoundException
	 */
	public int[] getDataIntList(String node1_id, String subnode, String key)
					throws NodeNotFoundException {
		DBElement node = getNode(node1_id, subnode, false);

		return ((node != null)
						? node.getEntryIntArrValue(key, null)
						: null);
	}

	/**
	 * Retrieve values of given node under specific path
	 *
	 * @param node1_id name of the node
	 * @param subnode path to the node
	 * @param key under which read the data
	 *
	 * @return array of Doubles
	 *
	 * @throws NodeNotFoundException
	 */
	public double[] getDataDoubleList(String node1_id, String subnode, String key)
					throws NodeNotFoundException {
		DBElement node = getNode(node1_id, subnode, false);

		return ((node != null)
						? node.getEntryDoubleArrValue(key, null)
						: null);
	}

	/**
	 * Retrieve value of given node under specific path
	 *
	 * @param node1_id name of the node
	 * @param subnode path to the node
	 * @param key under which read the data
	 * @param def default value if nothing is stored
	 *
	 * @return Object with value
	 *
	 * @throws NodeNotFoundException
	 */
	public Object getData(String node1_id, String subnode, String key, Object def)
					throws NodeNotFoundException {
		DBElement node = getNode(node1_id, subnode, false);

		return ((node != null)
						? node.getEntryValue(key, def)
						: null);
	}

	/**
	 * Retrieve value of given node under specific path
	 *
	 * @param node1_id name of the node
	 * @param subnode path to the node
	 * @param key under which read the data
	 * @param def default value if nothing is stored
	 *
	 * @return Integer value
	 *
	 * @throws NodeNotFoundException
	 */
	public int getDataInt(String node1_id, String subnode, String key, int def)
					throws NodeNotFoundException {
		DBElement node = getNode(node1_id, subnode, false);

		return ((node != null)
						? node.getEntryIntValue(key, def)
						: null);
	}

	/**
	 * Retrieve value of given node under specific path
	 *
	 * @param node1_id name of the node
	 * @param subnode path to the node
	 * @param key under which read the data
	 * @param def default value if nothing is stored
	 *
	 * @return Double value
	 *
	 * @throws NodeNotFoundException
	 */
	public double getDataDouble(String node1_id, String subnode, String key, double def)
					throws NodeNotFoundException {
		DBElement node = getNode(node1_id, subnode, false);

		return ((node != null)
						? node.getEntryDoubleValue(key, def)
						: null);
	}

	/**
	 * Retrieve value of given node under specific path
	 *
	 * @param node1_id name of the node
	 * @param subnode path to the node
	 * @param key under which read the data
	 *
	 * @return Object value
	 *
	 * @throws NodeNotFoundException
	 */
	public Object getData(String node1_id, String subnode, String key)
					throws NodeNotFoundException {
		return getData(node1_id, subnode, key, null);
	}

	/**
	 * Retrieve value of given node
	 *
	 * @param node1_id name of the node
	 * @param key under which read the data
	 *
	 * @return Double value
	 *
	 * @throws NodeNotFoundException
	 */
	public Object getData(String node1_id, String key) throws NodeNotFoundException {
		return getData(node1_id, null, key, null);
	}

	/**
	 * Retrieve list of subnodes under specific path
	 *
	 * @param node1_id name of the node
	 * @param subnode path to the node
	 *
	 * @return arrays of subnodes names
	 *
	 * @throws NodeNotFoundException
	 */
	public String[] getSubnodes(String node1_id, String subnode)
					throws NodeNotFoundException {

		DBElement node = getNode(node1_id, subnode, false);

		return ((node != null)
						? node.getSubnodes()
						: null);
	}

	/**
	 * Retrieve list of subnodes under root
	 *
	 * @param node1_id name of the node
	 *
	 * @return arrays of subnodes names
	 *
	 * @throws NodeNotFoundException
	 */
	public String[] getSubnodes(String node1_id) throws NodeNotFoundException {
		return getSubnodes(node1_id, null);
	}

	/**
	 * Retrieve list of keys under specific path
	 *
	 * @param node1_id name of the node
	 * @param subnode path to the node
	 *
	 * @return arrays of keys names
	 *
	 * @throws NodeNotFoundException
	 */
	public String[] getKeys(String node1_id, String subnode) throws NodeNotFoundException {
		DBElement node = getNode(node1_id, subnode, false);

		return ((node != null)
						? node.getEntryKeys()
						: null);
	}

	/**
	 * Retrieve list of keys under root
	 *
	 * @param node1_id name of the node
	 *
	 * @return arrays of subnodes names
	 *
	 * @throws NodeNotFoundException
	 */
	public String[] getKeys(String node1_id) throws NodeNotFoundException {
		return getKeys(node1_id, null);
	}

	/**
	 * Removes data of specific key from node of given name under given path
	 *
	 * @param node1_id name of the node
	 * @param subnode path to the node
	 * @param key name of the key
	 * @throws NodeNotFoundException
	 */
	public void removeData(String node1_id, String subnode, String key)
					throws NodeNotFoundException {
		DBElement node = getNode(node1_id, subnode, false);

		if (node != null) {
			node.removeEntry(key);
			saveDB();
		}
	}

	/**
	 * Removes data of specific key from node of given name under root element
	 *
	 * @param node1_id name of the node
	 * @param key name of the key
	 * @throws NodeNotFoundException
	 */
	public void removeData(String node1_id, String key) throws NodeNotFoundException {
		removeData(node1_id, null, key);
	}

	/**
	 * Removes node of given name under given path
	 *
	 * @param node1_id name of the node
	 * @param subnode path to the node
	 * @throws NodeNotFoundException
	 */
	public void removeSubnode(String node1_id, String subnode)
					throws NodeNotFoundException {
		DBElement node = getNode(node1_id, subnode, false);

		if (node != null) {
			node.removeNode(subnode);
			saveDB();
		}
	}

	/**
	 * Performs synchronization with the file
	 *
	 * @throws IOException
	 */
	public void sync() throws IOException {
		write();
	}

	/**
	 * Writes XMLDB to file
	 * @throws IOException
	 */
	private void write() throws IOException {
		lock.lock();
		try {
			String buffer           = root.formatedString(0, 1);
			OutputStreamWriter file = new OutputStreamWriter(new FileOutputStream(dbFile,
																	false), "UTF-8");

			if ( !memoryMode ){
				file.write( "<?xml version='1.0' encoding='UTF-8'?>\n" );
				file.write( buffer + "\n" );
				file.close();
			}
		} finally {
			lock.unlock();
		}    // end of try-finally
	}

	//~--- inner classes --------------------------------------------------------

	/**
	 * Helper class for comparing elements
	 */

	private static class DBElementComparator
					implements Comparator<DBElement>, Serializable {

		/** serialVersionUID */
		private static final long serialVersionUID = 1L;

		@Override
		public int compare(DBElement el1, DBElement el2) {
			String name1 = el1.getAttributeStaticStr("name");
			String name2 = el2.getAttributeStaticStr("name");

			return name1.compareTo(name2);
		}
	}

	/**
	 * Helper class for performing scheduled writes to file
	 */
	class DBSaver
					implements Runnable {

		/**
		 * Constructor...
		 */
		public DBSaver() {}

		@Override
		public void run() {
			while (true) {
				try {
					synchronized (db_saver) {
						db_saver.wait();
					}
					Thread.sleep(2000);
				} catch (InterruptedException e) {}
				try {
					sync();
				} catch (Exception e) {
					log.severe("Can't save repository file: " + e);
				}
			}    // end of while (true)
		}
	}
}    // XMLDB

