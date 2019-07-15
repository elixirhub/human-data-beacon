package org.ega_archive.elixircore.controller;

import org.ega_archive.elixircore.dto.Base;
import org.ega_archive.elixircore.dto.version.GitRepositoryState;
import org.ega_archive.elixircore.dto.version.MavenEntry;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

@RestController
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class VersionController {

  @RequestMapping(value = "/version/core", method = RequestMethod.GET)
  public Base<GitRepositoryState> getCoreVersion() {
    return getRepositoryState("core");
  }

  @RequestMapping(value = "/version", method = RequestMethod.GET)
  public Base<GitRepositoryState> getServiceVersion() throws IOException {
    return getRepositoryState("git");
  }

  private Base<GitRepositoryState> getRepositoryState(String filepreffix) {
    Properties properties = new Properties();
    try {
      properties.load(getClass().getClassLoader().getResourceAsStream(filepreffix + ".properties"));
    } catch (IOException e) {
      e.printStackTrace();
    }
    GitRepositoryState gitRepositoryState = new GitRepositoryState(properties);
    return new Base<GitRepositoryState>(gitRepositoryState);
  }

  @RequestMapping(value = "/version/dependencies", method = RequestMethod.GET)
  public Base<MavenEntry> getServiceDependencies() throws IOException {

    Properties properties = new Properties();
    List<MavenEntry> mavenDependencies = null;
    try {
      properties.load(getClass().getClassLoader().getResourceAsStream(
          "META-INF/maven/dependencies.properties"));
      Map<String, MavenEntry> dictionary = new HashMap<String, MavenEntry>(101);
      for (Object key : properties.keySet()) {
        String[] parts = ((String) key).split("/");
        if (parts.length < 3) {
          continue;
        }
        String dictionarykey = parts[0] + "/" + parts[1];
        MavenEntry mavenEntry = dictionary.get(dictionarykey);
        if (mavenEntry == null) {// if null create a new object
          mavenEntry = MavenEntry.builder().group(parts[0]).name(parts[1]).build();
        }
        if ("version".equals(parts[2])) {
          mavenEntry.setVersion(properties.getProperty((String) key));
        } else if ("scope".equals(parts[2])) {
          mavenEntry.setScope(properties.getProperty((String) key));
        } else if ("type".equals(parts[2])) {
          mavenEntry.setType(properties.getProperty((String) key));
        }
        dictionary.put(dictionarykey, mavenEntry);
      }
      mavenDependencies = new ArrayList<MavenEntry>(dictionary.values());
    } catch (IOException e) {
      e.printStackTrace();
    }

    return new Base<MavenEntry>(mavenDependencies);
  }

}
