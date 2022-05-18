package io.github.lvyahui8.owlet.graph;

import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Data
public class Graph implements Serializable {
    Map<String,MethodNode> nodeMap = new HashMap<>();
}
