package io.github.lvyahui8.owlet.graph;

import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.*;

@Data
public class MethodNode implements Serializable {

    String name;

    String declareClassFullName;

    List<String> paramTypeList = new LinkedList<>();

    Map<String,MethodNode> callerMap = new HashMap<>();
    Map<String,MethodNode> calleeMap = new HashMap<>();

    public MethodNode() {
    }


    public String getKey() {
        return StringUtils.join(declareClassFullName,name,StringUtils.join(paramTypeList,"_"),"_");
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof MethodNode)) {
            return false;
        }
        MethodNode that = (MethodNode) obj;
        return name.equals(that.name)
                && declareClassFullName.equals(that.declareClassFullName)
                && CollectionUtils.isEqualCollection(this.paramTypeList,that.paramTypeList);
    }
}
