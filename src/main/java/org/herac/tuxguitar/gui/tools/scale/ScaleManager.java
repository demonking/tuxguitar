package org.herac.tuxguitar.gui.tools.scale;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.herac.tuxguitar.gui.editors.chord.ChordSelector;
import org.herac.tuxguitar.gui.tools.scale.xml.ScaleReader;
import org.herac.tuxguitar.gui.util.TGFileUtils;
import org.herac.tuxguitar.gui.util.TGMusicKeyUtils;
import org.herac.tuxguitar.song.models.TGScale;

public class ScaleManager {
  private static final String[] KEY_NAMES = TGMusicKeyUtils
      .getSharpKeyNames(TGMusicKeyUtils.PREFIX_SCALE);

  private static final String KEY_SEPARATOR = ",";

  public static final int NONE_SELECTION = -1;

  private TGScale scale;

  private List<ScaleListener> scaleListeners;

  private List<ScaleInfo> scales;

  private int selectionIndex;

  private int selectionKey;

  public ScaleManager() {
    this.scales = new ArrayList<ScaleInfo>();
    this.scaleListeners = new ArrayList<ScaleListener>();
    this.scale = new TGScale();
    this.selectionKey = 0;
    this.selectionIndex = NONE_SELECTION;
    this.loadScales();
  }

  public void addListener(ScaleListener listener) {
    if (!this.scaleListeners.contains(listener)) {
      this.scaleListeners.add(listener);
    }
  }

  public int countScales() {
    return this.scales.size();
  }

  public void fireListeners() {
    for (int i = 0; i < this.scaleListeners.size(); i++) {
      ScaleListener listener = (ScaleListener) this.scaleListeners.get(i);
      listener.loadScale();
    }
  }

  public String getKeyName(int index) {
    if (index >= 0 && index < KEY_NAMES.length) {
      return KEY_NAMES[index];
    }
    return null;
  }

  public String[] getKeyNames() {
    return KEY_NAMES;
  }

  public TGScale getScale() {
    return this.scale;
  }

  public String getScaleKeys(int index) {
    if (index >= 0 && index < this.scales.size()) {
      return (((ScaleInfo) this.scales.get(index)).getKeys());
    }
    return null;
  }

  public String getScaleName(int index) {
    if (index >= 0 && index < this.scales.size()) {
      return (((ScaleInfo) this.scales.get(index)).getName());
    }
    return null;
  }

  public String[] getScaleNames() {
    String[] names = new String[this.scales.size()];
    for (int i = 0; i < this.scales.size(); i++) {
      ScaleInfo info = (ScaleInfo) this.scales.get(i);
      names[i] = info.getName();
    }
    return names;
  }

  public int getSelectionIndex() {
    return this.selectionIndex;
  }

  public int getSelectionKey() {
    return this.selectionKey;
  }

  private void loadScales() {
    try {
      new ScaleReader().loadScales(this.scales, TGFileUtils
          .getResourceAsStream("scales/scales.xml"));
    } catch (Throwable e) {
      LOG.error(e);
    }
  }
  

  /** The Logger for this class. */
  public static final transient Logger LOG = Logger
      .getLogger(ScaleManager.class);
  

  public void removeListener(ScaleListener listener) {
    if (this.scaleListeners.contains(listener)) {
      this.scaleListeners.remove(listener);
    }
  }

  public void selectScale(int index, int key) {
    if (index == NONE_SELECTION) {
      getScale().clear();
    } else if (index >= 0 && index < this.scales.size()) {
      getScale().clear();
      ScaleInfo info = (ScaleInfo) this.scales.get(index);
      String[] keys = info.getKeys().split(KEY_SEPARATOR);
      for (int i = 0; i < keys.length; i++) {
        int note = (Integer.parseInt(keys[i]) - 1);
        if (note >= 0 && note < 12) {
          getScale().setNote(note, true);
        }
      }
      getScale().setKey(key);
    }
    this.selectionIndex = index;
    this.selectionKey = key;
    this.fireListeners();
  }
}
