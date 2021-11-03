// 
// Decompiled by Procyon v0.5.36
// 
package hero.npc.service;

import java.util.Vector;
import hero.map.Map;

public class AStar {

    public static byte[] getPath(final short _startX, final short _startY, final short _endX, final short _endY, final short _moveMaxGridPerTime, final short _minGridOfTarget, final Map _map) {
        if (_startX == _endX && _startY == _endY) {
            return new byte[1];
        }
        Vector<cNode> vOpen = new Vector<cNode>(9, 1);
        Vector<cNode> vClose = new Vector<cNode>(9, 1);
        cNode cn = new cNode();
        cn.setNode(_startX, _startY, _endX, _endY, (byte) 2, null);
        vOpen.addElement(cn);
        boolean isFind = false;
        cNode minNode = null;
        int num = 0;
        while (vOpen.size() != 0) {
            minNode = vOpen.firstElement();
            if (minNode.X == _endX && minNode.Y == _endY) {
                isFind = true;
                break;
            }
            vOpen.removeElement(minNode);
            vClose.addElement(minNode);
            addOpen(minNode, vOpen, vClose, _endX, _endY, _map);
            taxis(vOpen, vClose);
            ++num;
        }
        vOpen.clear();
        vOpen = null;
        vClose.clear();
        vClose = null;
        if (!isFind) {
            return null;
        }
        byte[] completeActionDirection = new byte[minNode.getG()];
        completeActionDirection[completeActionDirection.length - 1] = minNode.D;
        for (int i = completeActionDirection.length - 2; i >= 0; --i) {
            minNode = minNode.parentNode;
            completeActionDirection[i] = minNode.D;
        }
        minNode = null;
        int actionDirectionLength = completeActionDirection.length - _minGridOfTarget;
        if (actionDirectionLength <= 0) {
            return new byte[1];
        }
        if (actionDirectionLength > _moveMaxGridPerTime) {
            actionDirectionLength = _moveMaxGridPerTime;
        }
        byte[] actionDirection = new byte[actionDirectionLength];
        System.arraycopy(completeActionDirection, 0, actionDirection, 0, actionDirectionLength);
        return actionDirection;
    }

    private AStar() {
    }

    private static void addOpen(final cNode _node, final Vector<cNode> _vOpen, final Vector<cNode> _vClose, final short _endX, final short _endY, final Map _map) {
        byte _dx = 0;
        byte _dy = 0;
        byte _D = 0;
        Label_0319:
        for (byte i = 0; i < 4; ++i) {
            boolean _isPass = true;
            cNode nodeNext = new cNode();
            if (i == 0) {
                _dx = (byte) (_node.X + 1);
                _dy = (byte) _node.Y;
                _D = 4;
            } else if (i == 1) {
                _dx = (byte) (_node.X - 1);
                _dy = (byte) _node.Y;
                _D = 3;
            } else if (i == 2) {
                _dx = (byte) _node.X;
                _dy = (byte) (_node.Y + 1);
                _D = 2;
            } else if (i == 3) {
                _dx = (byte) _node.X;
                _dy = (byte) (_node.Y - 1);
                _D = 1;
            }
            _isPass = _map.isRoad(_dx, _dy);
            if (_isPass) {
                for (int l = 0; l < _vClose.size(); ++l) {
                    cNode nodeHave = _vClose.elementAt(l);
                    if (_dx == nodeHave.X && _dy == nodeHave.Y) {
                        continue Label_0319;
                    }
                }
                nodeNext.setNode(_dx, _dy, _endX, _endY, _D, _node);
                int l = 0;
                while (l < _vOpen.size()) {
                    cNode nodeHave = _vOpen.elementAt(l);
                    if (_dx == nodeHave.X && _dy == nodeHave.Y) {
                        if (nodeHave.getG() > nodeNext.getG()) {
                            nodeHave.parentNode = _node;
                            nodeHave.D = nodeNext.D;
                        }
                        continue Label_0319;
                    } else {
                        ++l;
                    }
                }
                nodeNext.G = nodeNext.getG();
                _vOpen.addElement(nodeNext);
            }
        }
    }

    private static void taxis(final Vector<cNode> _vOpen, final Vector<cNode> _vClose) {
        int minF = 0;
        int id = -1;
        int tempF = 0;
        for (int i = _vOpen.size() - 1; i >= 0; --i) {
            tempF = _vOpen.elementAt(i).getF();
            if (tempF < minF || minF == 0) {
                minF = tempF;
                id = i;
            }
        }
        if (id != -1) {
            cNode _tempcNode = _vOpen.elementAt(id);
            _vOpen.removeElementAt(id);
            _vOpen.insertElementAt(_tempcNode, 0);
        }
    }

    private static class cNode {

        byte F;
        int G;
        byte H;
        short X;
        short Y;
        byte D;
        cNode parentNode;

        private void setNode(final short _x, final short _y, final short _endX, final short _endY, final byte _d, final cNode _fNode) {
            this.X = _x;
            this.Y = _y;
            this.D = _d;
            this.H = (byte) (Math.abs(this.X - _endX) + Math.abs(this.Y - _endY));
            this.parentNode = _fNode;
        }

        private int getG() {
            if (this.parentNode == null) {
                return this.G = 1;
            }
            return this.G = this.parentNode.G + 1;
        }

        private int getF() {
            return this.getG() + this.H;
        }
    }
}
