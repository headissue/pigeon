/*
 * Copyright (C) 2013 headissue GmbH (www.headissue.com)
 *
 * Source repository: https://github.com/headissue/pigeon
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This patch is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this patch.  If not, see <http://www.gnu.org/licenses/agpl.txt/>.
 */
package com.headissue.pigeon.service;


import com.google.inject.Singleton;
import com.headissue.pigeon.PigeonException;
import org.codehaus.jackson.map.AnnotationIntrospector;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.codehaus.jackson.xc.JaxbAnnotationIntrospector;

@Singleton
public class JacksonService {

  private final ObjectMapper mapper;

  public JacksonService() {
    mapper = new ObjectMapper();
    final AnnotationIntrospector introspector = new JaxbAnnotationIntrospector();

    mapper.setDeserializationConfig(
      mapper.getDeserializationConfig().withAnnotationIntrospector(introspector)
        .without(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES)
    );
    mapper.setSerializationConfig(mapper.getSerializationConfig().withAnnotationIntrospector(introspector));
  }

  public String toString(Object value) {
    try {
      return mapper.writeValueAsString(value);
    } catch (Exception e) {
      throw new PigeonException("value can not be serialized", e);
    }
  }

  public <T> T fromString(String content, Class<T> type) {
    try {
      return mapper.readValue(content, type);
    } catch (Exception e) {
      throw new PigeonException("object can not be deserialized", e);
    }
  }

  /**
   * Read the string into a generic list
   * <pre>
   *   List<Bean> list = jackson.fromString(content, new TypeReference<Bean>() {} );
   * </pre>
   */
  public <T> T fromString(String content, TypeReference<T> type) {
    try {
      return mapper.readValue(content, type);
    } catch (Exception e) {
      throw new PigeonException("object can not be deserialized", e);
    }

  }
}
