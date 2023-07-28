package com.gaoda.io;


/**
 * 定义一个Resource类型表示文件
 * @param path
 * @param name
 */
public record Resource(String path, String name) {

//    @Override
//    public String toString() {
//        return "Resource{" +
//                "path='" + path + '\'' +
//                ", name='" + name + '\'' +
//                '}';
//    }
//
//    @Override
//    public boolean equals(Object o) {
//        if (this == o) return true;
//        if (o == null || getClass() != o.getClass()) return false;
//        Resource resource = (Resource) o;
//        return Objects.equals(path, resource.path) &&
//                Objects.equals(name, resource.name);
//    }

}