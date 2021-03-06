
/*
This Java source file was generated by test-to-java.xsl
and is a derived work from the source document.
The source document contained the following notice:



Copyright (c) 2001 World Wide Web Consortium, 
(Massachusetts Institute of Technology, Institut National de
Recherche en Informatique et en Automatique, Keio University).  All 
Rights Reserved.  This program is distributed under the W3C's Software
Intellectual Property License.  This program is distributed in the 
hope that it will be useful, but WITHOUT ANY WARRANTY; without even
the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR 
PURPOSE.  

See W3C License http://www.w3.org/Consortium/Legal/ for more details.


*/

package tests.org.w3c.dom;

import dalvik.annotation.TestTargets;
import dalvik.annotation.TestLevel;
import dalvik.annotation.TestTargetNew;
import dalvik.annotation.TestTargetClass;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.DOMException;

import javax.xml.parsers.DocumentBuilder;

/**
 *     The "setNamedItemNS(arg)" method for a 
 *    NamedNodeMap should raise INUSE_ATTRIBUTE_ERR DOMException if 
 *    arg is an Attr that is already an attribute of another Element object.
 *    
 *    Retrieve an attr node from the third "address" element whose local name
 *    is "domestic" and namespaceURI is "http://www.netzero.com".
 *    Invoke method setNamedItemNS(arg) on the map of the first "address" element with
 *    arg being the attr node from above.  Method should raise
 *    INUSE_ATTRIBUTE_ERR DOMException.
* @author NIST
* @author Mary Brady
* @see <a href="http://www.w3.org/TR/DOM-Level-2-Core/core#xpointer(id('ID-258A00AF')/constant[@name='INUSE_ATTRIBUTE_ERR'])">http://www.w3.org/TR/DOM-Level-2-Core/core#xpointer(id('ID-258A00AF')/constant[@name='INUSE_ATTRIBUTE_ERR'])</a>
* @see <a href="http://www.w3.org/TR/DOM-Level-2-Core/core#ID-setNamedItemNS">http://www.w3.org/TR/DOM-Level-2-Core/core#ID-setNamedItemNS</a>
* @see <a href="http://www.w3.org/TR/DOM-Level-2-Core/core#xpointer(id('ID-setNamedItemNS')/raises/exception[@name='DOMException']/descr/p[substring-before(.,':')='INUSE_ATTRIBUTE_ERR'])">http://www.w3.org/TR/DOM-Level-2-Core/core#xpointer(id('ID-setNamedItemNS')/raises/exception[@name='DOMException']/descr/p[substring-before(.,':')='INUSE_ATTRIBUTE_ERR'])</a>
*/
@TestTargetClass(NamedNodeMap.class) 
public final class SetNamedItemNS extends DOMTestCase {

    DOMDocumentBuilderFactory factory;

    DocumentBuilder builder;

    protected void setUp() throws Exception {
        super.setUp();
        try {
            factory = new DOMDocumentBuilderFactory(DOMDocumentBuilderFactory
                    .getConfiguration2());
            builder = factory.getBuilder();
        } catch (Exception e) {
            fail("Unexpected exception" + e.getMessage());
        }
    }

    protected void tearDown() throws Exception {
        factory = null;
        builder = null;
        super.tearDown();
    }

   /**
    * Runs the test case.
    * @throws Throwable Any uncaught exception causes test to fail
    */
    @TestTargetNew(
        level = TestLevel.PARTIAL_COMPLETE,
        notes = "Verifies DOMException with INUSE_ATTRIBUTE_ERR code.",
        method = "setNamedItemNS",
        args = {org.w3c.dom.Node.class}
    )
   public void testSetNamedItemNS1() throws Throwable {
      Document doc;
      NodeList elementList;
      Node anotherElement;
      NamedNodeMap anotherMap;
      Node arg;
      Node testAddress;
      NamedNodeMap map;
      
      doc = (Document) load("staffNS", builder);
      elementList = doc.getElementsByTagName("address");
      anotherElement = elementList.item(2);
      anotherMap = anotherElement.getAttributes();
      arg = anotherMap.getNamedItemNS("http://www.netzero.com", "domestic");
      testAddress = elementList.item(0);
      map = testAddress.getAttributes();
      
      {
         boolean success = false;
         try {
            map.setNamedItemNS(arg);
          } catch (DOMException ex) {
            success = (ex.code == DOMException.INUSE_ATTRIBUTE_ERR);
         }
         assertTrue("throw_INUSE_ATTRIBUTE_ERR", success);
      }
}
    @TestTargetNew(
        level = TestLevel.PARTIAL_COMPLETE,
        notes = "Verifies DOMException with WRONG_DOCUMENT_ERR code.",
        method = "setNamedItemNS",
        args = {org.w3c.dom.Node.class}
    )
   public void testSetNamedItemNS2() throws Throwable {
          String namespaceURI = "http://www.usa.com";
          String qualifiedName = "dmstc:domestic";
          Document doc;
          Document anotherDoc;
          Node arg;
          NodeList elementList;
          Node testAddress;
          NamedNodeMap attributes;
         
          doc = (Document) load("staffNS", builder);
          anotherDoc = (Document) load("staffNS", builder);
          arg = anotherDoc.createAttributeNS(namespaceURI, qualifiedName);
          arg.setNodeValue("Maybe");
          elementList = doc.getElementsByTagName("address");
          testAddress = elementList.item(0);
          attributes = testAddress.getAttributes();
          
          {
             boolean success = false;
             try {
                attributes.setNamedItemNS(arg);
              } catch (DOMException ex) {
                success = (ex.code == DOMException.WRONG_DOCUMENT_ERR);
             }
             assertTrue("throw_WRONG_DOCUMENT_ERR", success);
          }
    }
    @TestTargetNew(
        level = TestLevel.PARTIAL_COMPLETE,
        notes = "Verifies positive fnctionality.",
        method = "getNamedItemNS",
        args = {java.lang.String.class, java.lang.String.class}
    )
   public void testSetNamedItemNS3() throws Throwable {
          String namespaceURI = "http://www.nist.gov";
          String qualifiedName = "prefix:newAttr";
          Document doc;
          Node arg;
          NodeList elementList;
          Node testAddress;
          NamedNodeMap attributes;
          Node retnode;
          String value;
          
          doc = (Document) load("staffNS", builder);
          arg = doc.createAttributeNS(namespaceURI, qualifiedName);
          arg.setNodeValue("newValue");
          elementList = doc.getElementsByTagName("address");
          testAddress = elementList.item(0);
          attributes = testAddress.getAttributes();
          attributes.setNamedItemNS(arg);
          retnode = attributes.getNamedItemNS(namespaceURI, "newAttr");
          value = retnode.getNodeValue();
          assertEquals("throw_Equals", "newValue", value);
          }

// Assumes validation.
//   public void testSetNamedItemNS4() throws Throwable {
//          String namespaceURI = "http://www.w3.org/2000/xmlns/";
//          String localName = "local1";
//          Document doc;
//          NodeList elementList;
//          Node testAddress;
//          NodeList nList;
//          Node child;
//          NodeList n2List;
//          Node child2;
//          NamedNodeMap attributes;
//          Node arg;
//          
//          int nodeType;
//          doc = (Document) load("staffNS", builder);
//          elementList = doc.getElementsByTagName("gender");
//          testAddress = elementList.item(2);
//          nList = testAddress.getChildNodes();
//          child = nList.item(0);
//          nodeType = (int) child.getNodeType();
//          
//          if (1 == nodeType) {
//              child = doc.createEntityReference("ent4");
//          assertNotNull("createdEntRefNotNull", child);
//          }
//        n2List = child.getChildNodes();
//          child2 = n2List.item(0);
//          assertNotNull("notnull", child2);
//          attributes = child2.getAttributes();
//          arg = attributes.getNamedItemNS(namespaceURI, localName);
//          
//          {
//             boolean success = false;
//             try {
//                attributes.setNamedItemNS(arg);
//              } catch (DOMException ex) {
//                success = (ex.code == DOMException.NO_MODIFICATION_ALLOWED_ERR);
//             }
//             assertTrue("throw_NO_MODIFICATION_ALLOWED_ERR", success);
//          }
//    }
    @TestTargetNew(
        level = TestLevel.PARTIAL_COMPLETE,
        notes = "Verifies positive fnctionality.",
        method = "setNamedItemNS",
        args = {org.w3c.dom.Node.class}
    )
   public void testSetNamedItemNS5() throws Throwable {
          String namespaceURI = "http://www.usa.com";
          String qualifiedName = "dmstc:domestic";
          Document doc;
          Node arg;
          NodeList elementList;
          Node testAddress;
          NamedNodeMap attributes;
          Node retnode;
          String value;
          doc = (Document) load("staffNS", builder);
          arg = doc.createAttributeNS(namespaceURI, qualifiedName);
          arg.setNodeValue("newValue");
          elementList = doc.getElementsByTagName("address");
          testAddress = elementList.item(0);
          attributes = testAddress.getAttributes();
          retnode = attributes.setNamedItemNS(arg);
          value = retnode.getNodeValue();
          assertEquals("throw_Equals", "Yes", value);
          }
  
}

