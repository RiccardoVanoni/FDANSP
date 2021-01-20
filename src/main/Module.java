package main;

import rendering.RenderHelper;

interface Module {
	
	void render(RenderHelper renderer);
	void update(double elapsedTime);
}
