package com.telefonica.euro_iaas.sdc.pupperwrapper.services.integration.tests;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.NoSuchElementException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.data.mongodb.core.MongoTemplate;

import com.mongodb.Mongo;
import com.telefonica.euro_iaas.sdc.pupperwrapper.services.tests.CatalogManagerMongoImpl4Test;
import com.telefonica.euro_iaas.sdc.puppetwrapper.common.Action;
import com.telefonica.euro_iaas.sdc.puppetwrapper.data.Node;
import com.telefonica.euro_iaas.sdc.puppetwrapper.data.Software;
import com.telefonica.euro_iaas.sdc.puppetwrapper.services.impl.ActionsServiceImpl;

import de.flapdoodle.embed.mongo.MongodExecutable;
import de.flapdoodle.embed.mongo.MongodProcess;
import de.flapdoodle.embed.mongo.MongodStarter;
import de.flapdoodle.embed.mongo.config.MongodConfig;
import de.flapdoodle.embed.mongo.config.RuntimeConfig;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.process.extract.UserTempNaming;
import de.flapdoodle.embed.process.runtime.Network;


public class CatalogManagerMongoIntegrationTest {

    
    private static CatalogManagerMongoImpl4Test catalogManagerMongo;

    private static final String LOCALHOST = "127.0.0.1";
    private static final String DB_NAME = "itest";
//    private static final int MONGO_TEST_PORT = 12345;
    private static MongodProcess mongoProcess;
    private static Mongo mongo;
    
    private MongoTemplate template;
    
    @BeforeClass
    public static void initializeDB() throws IOException {
        
        System.out.println("Init embedded DB");
        
        RuntimeConfig config = new RuntimeConfig();
        config.setExecutableNaming(new UserTempNaming());
        
        System.out.println("Init embedded DB - 1"); 
        
        MongodStarter starter = MongodStarter.getInstance(config);
        
        System.out.println("Init embedded DB - 2");
        
        int port = Network.getFreeServerPort();

        MongodExecutable mongoExecutable = starter.prepare(new MongodConfig(Version.V2_2_0, port, false));
        mongoProcess = mongoExecutable.start();
        
        System.out.println("Init embedded DB - 3");
        
        mongo = new Mongo(LOCALHOST, port);
        mongo.getDB(DB_NAME);
        
        System.out.println("Init OK");
    }

    @AfterClass
    public static void shutdownDB() throws InterruptedException {
        System.out.println("shutting down embedded DB");
        mongo.close();
        mongoProcess.stop();
        System.out.println("shut down OK");
    }

    
    @Before
    public void setUp() throws Exception {
        catalogManagerMongo=new CatalogManagerMongoImpl4Test();
        template = new MongoTemplate(mongo, DB_NAME);
        catalogManagerMongo.setMongoTemplate(template);
    }

    @After
    public void tearDown() throws Exception {
        template.dropCollection(Node.class);
    }

    @Test(expected = NoSuchElementException.class)
    public void getNodeTest_notfound() {
        Node node = catalogManagerMongo.getNode("test");

    }

    @Test
    public void getNodeTest() {
        Node node = new Node();
        node.setId("test");
        node.setGroupName("group");
        catalogManagerMongo.addNode(node);
        Node node1 = catalogManagerMongo.getNode("test");
        assertTrue(node1.getId().equals("test"));
    }

    @Test
    public void testAddNode() {
        int length = catalogManagerMongo.getNodeLength();
        assertTrue(length == 0);
        Node node = new Node();
        node.setId("test");
        node.setGroupName("group");
        catalogManagerMongo.addNode(node);
        length = catalogManagerMongo.getNodeLength();
        assertTrue(length == 1);
    }

    @Test
    public void testRemoveNode() {
        int length = catalogManagerMongo.getNodeLength();
        assertTrue(length == 0);
        Node node = new Node();
        node.setId("test");
        node.setGroupName("group");
        catalogManagerMongo.addNode(node);
        length = catalogManagerMongo.getNodeLength();
        assertTrue(length == 1);

        catalogManagerMongo.removeNode(node.getId());
        length = catalogManagerMongo.getNodeLength();
        assertTrue(length == 0);
    }

    @Test
    public void generateFileStrTest_onlyNode() {
        Node node = new Node();
        node.setId("test");
        node.setGroupName("group");
        catalogManagerMongo.addNode(node);

        String str = catalogManagerMongo.generateManifestStr("test");
        assertTrue(str.length() > 0);
        assertTrue(str.contains("{"));
        assertTrue(str.contains("node"));
    }

    @Test
    public void generateFileStrTest_nodeAndSoft() {
        Node node = new Node();
        node.setId("test");
        node.setGroupName("group");

        Software soft = new Software();
        soft.setName("testSoft");
        soft.setVersion("1.0.0");
        soft.setAction(Action.INSTALL);

        node.addSoftware(soft);

        catalogManagerMongo.addNode(node);

        String str = catalogManagerMongo.generateManifestStr("test");
        assertTrue(str.length() > 0);
        assertTrue(str.contains("{"));
        assertTrue(str.contains("node"));
        assertTrue(str.contains("class"));
        assertTrue(str.contains("install"));
        assertTrue(str.contains("version"));
    }

    @Test
    public void generateSiteFile() {
        Node node = new Node();
        node.setId("test");
        node.setGroupName("group");

        Node node2 = new Node();
        node2.setId("test2");
        node2.setGroupName("group2");

        catalogManagerMongo.addNode(node);
        catalogManagerMongo.addNode(node2);

        String str = catalogManagerMongo.generateSiteStr();

        assertTrue(str.length() > 0);
        assertTrue(str.contains("import 'group/*.pp'"));
        assertTrue(str.contains("import 'group2/*.pp'"));
    }

    @Test
    public void removeNodesByGroupNameTest() {

        Node node = new Node();
        node.setId("test");
        node.setGroupName("group");

        Node node2 = new Node();
        node2.setId("test2");
        node2.setGroupName("group");

        catalogManagerMongo.addNode(node);
        catalogManagerMongo.addNode(node2);

        assertTrue(catalogManagerMongo.getNodeLength() == 2);

        catalogManagerMongo.removeNodesByGroupName("group");

        assertTrue(catalogManagerMongo.getNodeLength() == 0);

    }
}