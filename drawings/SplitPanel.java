package drawings;

import java.awt.*;
import javax.swing.*;

public class SplitPanel extends JPanel {
	private JPanel[] children;
	private boolean[] initialized;

	public SplitPanel(int numChildren, boolean isHorizontal) {
		GroupLayout layout = new GroupLayout(this);
		setLayout(layout);
		//layout.setAutoCreateGaps(true);
		//layout.setAutoCreateContainerGaps(true);

		children = new JPanel[numChildren];
		initialized = new boolean[numChildren];

		GroupLayout.Group hGroup = layout.createSequentialGroup();
		GroupLayout.Group vGroup = layout.createParallelGroup();

		if (!isHorizontal) {
			hGroup = layout.createParallelGroup();
			vGroup = layout.createSequentialGroup();
		}

		for (int i = 0; i < numChildren; ++i) {
			JPanel dummy = new JPanel();
			hGroup.addComponent(dummy);
			vGroup.addComponent(dummy);
			add(dummy);
			children[i] = dummy;
		}
		layout.setHorizontalGroup(hGroup);
		layout.setVerticalGroup(vGroup);

		setOpaque(false);
	}

	public void setChild(int index, JComponent comp) {
		if (index < 0 || index > children.length)
			throw new IllegalArgumentException("Invalid index to HPanel");
		if (initialized[index])
			throw new IllegalArgumentException("Index already initialized");
		children[index].add(comp);
		initialized[index] = true;
	}
}