/**
 * Copyright 2010-2016 OrientDB LTD (http://orientdb.com)
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * <p>
 * For more information: http://www.orientdb.com
 */
package com.orientechnologies.spatial;

import com.orientechnologies.orient.core.db.ODatabaseType;
import com.orientechnologies.orient.core.db.OrientDB;
import com.orientechnologies.orient.core.db.OrientDBConfig;
import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.executor.OResult;
import com.orientechnologies.orient.core.sql.executor.OResultSet;
import com.orientechnologies.spatial.shape.legacy.OPointLegecyBuilder;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;

/**
 * Created by Enrico Risa on 14/08/15.
 */
public class LuceneSpatialFunctionAsGeoJSONTest {

  ODatabaseDocument db;
  OrientDB          orientDB;

  @Before
  public void before() {
    orientDB = new OrientDB("embedded:.", OrientDBConfig.defaultConfig());
    orientDB.create("test", ODatabaseType.MEMORY);
    db = orientDB.open("test", "admin", "admin");

  }

  @After
  public void after() {
    db.close();
    orientDB.drop("test");
    orientDB.close();
  }

  @Test
  public void geoPointTest() {
    queryAndMatch("POINT(11.11111 12.22222)", "{\"type\":\"Point\",\"coordinates\":[11.11111,12.22222]}");
  }

  @Test
  public void geoLineStringTest() {
    queryAndMatch("LINESTRING(1 2 , 3 4)", "{\"type\":\"LineString\",\"coordinates\":[[1,2],[3,4]]}");
  }

  protected void queryAndMatch(String wkt, String geoJson) {

    OResultSet query = db.query("SELECT ST_GeomFromText(:wkt) as wkt,ST_GeomFromGeoJSON(:geoJson) as geoJson;", new HashMap() {{
      put("geoJson", geoJson);
      put("wkt", wkt);
    }});
    OResult result = query.stream().findFirst().get();
    ODocument jsonGeom = result.getProperty("geoJson");
    ODocument wktGeom = result.getProperty("wkt");
    assertGeometry(wktGeom, jsonGeom);
  }

  private void assertGeometry(ODocument source, ODocument geom) {
    Assert.assertNotNull(geom);

    Assert.assertNotNull(geom.field("coordinates"));

    Assert.assertEquals(source.getClassName(), geom.getClassName());
    Assert.assertEquals(geom.<OPointLegecyBuilder>field("coordinates"), source.field("coordinates"));

  }
}
