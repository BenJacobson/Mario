package mario;

import util.Images;

import java.awt.*;

class MarioFrames {

	private Image	[/* small, big, fire */]
					[/* stand, jump, run1, run2, run3, run4, turn, dead*/]
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
						// turn frames
						{
								Images.turn_frame_back, Images.turn_frame
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
								Images.jump_frame_big_back, Images.jump_frame_big
						},
						// running 1 frames
						{
								Images.run_frame_13_big_back, Images.run_frame_13_big
						},
						// running 2 frames
						{
								Images.run_frame_2_big_back, Images.run_frame_2_big
						},
						// running 3 frames
						{
								Images.run_frame_13_big_back, Images.run_frame_13_big
						},
						// running 4 frames
						{
								Images.run_frame_4_big_back, Images.run_frame_4_big
						},
						// turning frames
						{
								Images.stand_frame_back_big, Images.stand_frame_big
						},
						// dead frames
						{
								Images.dead_frame, Images.dead_frame
						}
				},
				// fire mario frames
				{
						// standing frames
						{
								Images.stand_frame_back_fire, Images.stand_frame_fire
						},
						// jumping frames
						{
								Images.jump_frame_fire_back, Images.jump_frame_fire
						},
						// running 1 frames
						{
								Images.run_frame_13_fire_back, Images.run_frame_13_fire
						},
						// running 2 frames
						{
								Images.run_frame_2_fire_back, Images.run_frame_2_fire
						},
						// running 3 frames
						{
								Images.run_frame_13_fire_back, Images.run_frame_13_fire
						},
						// running 4 frames
						{
								Images.run_frame_4_fire_back, Images.run_frame_4_fire
						},
						// turning frames
						{
								Images.stand_frame_back_fire, Images.stand_frame_fire
						},
						// dead frames
						{
								Images.dead_frame, Images.dead_frame
						}
				}
		};
	}

	Image getFrame(Mario.PowerState powerState, Mario.FrameState frameState, boolean lastDirectionForward, boolean shoot) {
		if ( shoot && powerState == Mario.PowerState.FIRE ) {
			frameState = Mario.FrameState.RUN2;
		}
		return frames[powerState.ordinal()][frameState.ordinal()][lastDirectionForward ? 1 : 0];
	}
}
