package drawings;

import java.awt.*;
import java.util.*;
import javax.swing.*;

public class SplitPanel extends JPanel {
	private ArrayList<JComponent> children;

	private GroupLayout layout;
	private GroupLayout.Group hGroup, vGroup;

	public SplitPanel(boolean isHorizontal) {
		layout = new GroupLayout(this);
		//layout.setAutoCreateGaps(true);
		//layout.setAutoCreateContainerGaps(true);

		children = new ArrayList<JComponent>();

		hGroup = layout.createSequentialGroup();
		vGroup = layout.createParallelGroup();

		if (!isHorizontal) {
			hGroup = layout.createParallelGroup();
			vGroup = layout.createSequentialGroup();
		}

		setOpaque(false);
	}

	public void addChild(JComponent comp) {
		JPanel dummy = new JPanel();
		dummy.add(comp);
		hGroup.addComponent(dummy);
		vGroup.addComponent(dummy);
		add(dummy);
		children.add(comp);
	}

	public void initLayout() {
		for (JComponent comp : children) {
			if (comp instanceof SplitPanel)
				((SplitPanel)comp).initLayout();
		}

		layout.setHorizontalGroup(hGroup);
		layout.setVerticalGroup(vGroup);
		setLayout(layout);
	}

	public void startAnimation() {
		for (JComponent comp : children) {
			if (comp instanceof SplitPanel)
				((SplitPanel)comp).startAnimation();
			else if (comp instanceof AnimatingPanel)
				((AnimatingPanel)comp).startAnimation();
		}
	}
}