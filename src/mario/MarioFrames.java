package mario;

import util.Images;

import java.awt.*;

class MarioFrames {

	private Image	[/* small, big, fire */]
					[/* stand, jump, run1, run2, run3, run4, dead*/]
					[/* back, forward */]
			frames = initFramesStructure();

	private Image[][][] initFramesStructure() {
		return new Image[][][] {
				// small mario frame
				{
						// standing frames
						{
								Images.stand_frame_back, Images.stand_frame
						},
						// jumping frames
						{
								Images.jump_frame_back, Images.jump_frame
						},
						// running 1 frames
						{
								Images.run_frame_13_back, Images.run_frame_13
						},
						// running 2 frames
						{
								Images.run_frame_2_back, Images.run_frame_2
						},
						// running 3 frames
						{
								Images.run_frame_13_back, Images.run_frame_13
						},
						// running 4 frames
						{
								Images.run_frame_4_back, Images.run_frame_4
						},
						// dead frames
						{
								Images.dead_frame, Images.dead_frame
						}
				},
				// big mario frames
				{
						// standing frames
						{
								Images.stand_frame_back_big, Images.stand_frame_big
						},
						// jumping frames
						{
								Images.stand_frame_back_big, Images.stand_frame_big
						},
						// running 1 frames
						{
								Images.stand_frame_back_big, Images.stand_frame_big
						},
						// running 2 frames
						{
								Images.stand_frame_back_big, Images.stand_frame_big
						},
						// running 3 frames
						{
								Images.stand_frame_back_big, Images.stand_frame_big
						},
						// running 4 frames
						{
								Images.stand_frame_back_big, Images.stand_frame_big
						},
						// dead frames
						{
								Images.stand_frame_back_big, Images.stand_frame_big
						}
				},
				// fire mario frames
				{
						// standing frames
						{
								Images.stand_frame_back_big, Images.stand_frame_big
						},
						// jumping frames
						{
								Images.stand_frame_back_big, Images.stand_frame_big
						},
						// running 1 frames
						{
								Images.stand_frame_back_big, Images.stand_frame_big
						},
						// running 2 frames
						{
								Images.stand_frame_back_big, Images.stand_frame_big
						},
						// running 3 frames
						{
								Images.stand_frame_back_big, Images.stand_frame_big
						},
						// running 4 frames
						{
								Images.stand_frame_back_big, Images.stand_frame_big
						},
						// dead frames
						{
								Images.stand_frame_back_big, Images.stand_frame_big
						}
				}
		};
	}

	public Image getFrame(Mario.PowerState powerState, Mario.FrameState frameState, boolean lastDirectionForward) {
		return frames[powerState.ordinal()][frameState.ordinal()][lastDirectionForward ? 1 : 0];
	}
}