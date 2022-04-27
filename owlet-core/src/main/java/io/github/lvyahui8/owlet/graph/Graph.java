package io.github.lvyahui8.owlet.graph;

import lombok.Data;

import java.io.Serializable;
import java.util.Set;

@Data
public class Graph implements Serializable {
    Set<Node> nodes;
    Set<Node> roots;
}
