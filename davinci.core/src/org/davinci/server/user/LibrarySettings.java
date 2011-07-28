package org.davinci.server.user;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import org.davinci.ajaxLibrary.LibInfo;
import org.davinci.ajaxLibrary.Library;
import org.davinci.server.IDavinciServerConstants;
import org.davinci.server.ServerManager;
import org.davinci.server.util.XMLFile;
import org.w3c.dom.Element;

public class LibrarySettings extends XMLFile {

    File      libFile;
    LibInfo[] libs;

    public LibrarySettings(File dir) {
        libFile = new File(dir, IDavinciServerConstants.LIBS_FILE);

        if (!libFile.exists()) {
            libs = getAllDefaultLibs();
            this.save();
        } else {
            ArrayList list = this.load(libFile);
            libs = (LibInfo[]) list.toArray(new LibInfo[list.size()]);
        }
    }

    public static LibInfo[] getAllDefaultLibs() {
        Library[] all = ServerManager.getServerManger().getLibraryManager().getAllLibraries();
        LibInfo[] results = new LibInfo[all.length];
        for (int i = 0; i < all.length; i++) {
            results[i] = new LibInfo(all[i].getID(), all[i].getName(), all[i].getVersion(), all[i].getDefaultRoot(), ".");
        }
        return results;
    }

    protected String getRootTag() {
        return "libraries";
    }

    public LibInfo[] allLibs() {
        return this.libs;
    }

    protected String getElementTag() {
        return "library";
    }

    public boolean addLibrary(String name, String version, String id, String virtualRoot, String base) {

        LibInfo link = new LibInfo(id, name, version, virtualRoot, base);

        LibInfo[] newLibs = new LibInfo[libs.length + 1];
        System.arraycopy(libs, 0, newLibs, 0, libs.length);
        newLibs[libs.length] = link;
        this.libs = newLibs;
        this.save();
        return true;
    }

    public boolean removeLibrary(String id, String version, String base) {
        for (int i = 0; i < this.libs.length; i++) {
            if (this.libs[i].getId().equals(id) && this.libs[i].getVersion().equals(version) && this.libs[i].getBase().equals(base)) {
                LibInfo[] newLinks = new LibInfo[libs.length - 1];
                System.arraycopy(libs, 0, newLinks, 0, i);
                System.arraycopy(libs, i + 1, newLinks, i, libs.length - 1 - i);
                this.libs = newLinks;
                this.save();
                return true;
            }
        }
        return false;

    }

    public void modifyLibrary(String id, String version, String virtualRoot, String base) {
        this.removeLibrary(id, version, base);
        this.addLibrary(id, version, id, virtualRoot, base);
    }

    protected Object createObject(Element element, String[] attributes) {

        LibInfo link = new LibInfo(attributes[0], attributes[1], attributes[2], attributes[3], attributes[4]);
        return link;
    }

    protected String[] getAttributeNames() {
        return new String[] { "id", "name", "version", "virtualRoot", "base" };
    }

    protected String[] getAttributeValues(Object object) {
        LibInfo link = (LibInfo) object;

        return new String[] { link.getId(), link.getName(), link.getVersion(), link.getVirtualRoot() };
    }

    private void save() {
        this.save(libFile, Arrays.asList(this.libs));
    }
}
