package org.kelog.japroj.platform;

class PlatformLibraryNotFoundException extends RuntimeException {
    PlatformLibraryNotFoundException(Throwable e) {
        super(e);
    }
}
