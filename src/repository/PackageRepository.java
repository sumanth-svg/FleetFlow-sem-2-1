package repository;

import java.util.ArrayList;
import java.util.List;
import model.Package;

/**
 * Repository class for storing and querying Package records.
 */
public class PackageRepository {

    private List<Package> packages;

    public PackageRepository() {
        this.packages = new ArrayList<>();
    }

    public void addPackage(Package pkg) {
        packages.add(pkg);
    }

    public List<Package> getAllPackages() {
        return packages;
    }

    public Package findByCode(String packageCode) {
        if (packageCode == null) return null;
        for (Package pkg : packages) {
            if (pkg.getPackageCode().equalsIgnoreCase(packageCode)) {
                return pkg;
            }
        }
        return null;
    }

    public boolean deletePackage(String packageCode) {
        Package pkg = findByCode(packageCode);
        if (pkg != null) {
            packages.remove(pkg);
            return true;
        }
        return false;
    }

    public int getPackageCount() {
        return packages.size();
    }

    public void clearAll() {
        packages.clear();
    }
}
