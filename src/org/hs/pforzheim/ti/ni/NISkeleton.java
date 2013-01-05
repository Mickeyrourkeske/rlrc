/**
 * This file is part of RLRC.
 * 
 * RLRC is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * RLRC is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with RLRC. If not, see <http://www.gnu.org/licenses/>.
 */
package org.hs.pforzheim.ti.ni;

import org.OpenNI.CalibrationProgressEventArgs;
import org.OpenNI.IObservable;
import org.OpenNI.IObserver;
import org.OpenNI.PoseDetectionCapability;
import org.OpenNI.SkeletonCapability;
import org.OpenNI.SkeletonProfile;
import org.OpenNI.StatusException;
import org.OpenNI.UserGenerator;

/**
 * @author schrob
 *
 */
public class NISkeleton extends NI implements Runnable {

	private PoseDetectionCapability poseDetectionCapability;
	private SkeletonCapability skeletonCapability;
	private String calibrationPose;
	
	public NISkeleton() {
		super();
		try {
			if(userGenerator == null)
				userGenerator = UserGenerator.create(context);
			
			poseDetectionCapability = userGenerator.getPoseDetectionCapability();
			
			skeletonCapability = userGenerator.getSkeletonCapability();
			calibrationPose = skeletonCapability.getSkeletonCalibrationPose();
			skeletonCapability.setSkeletonProfile(SkeletonProfile.ALL);
			
			skeletonCapability.getCalibrationCompleteEvent().addObserver(new IObserver<CalibrationProgressEventArgs>() {
				
				@Override
				public void update(IObservable<CalibrationProgressEventArgs> observable, CalibrationProgressEventArgs args) {
					int id = args.getUser();
					System.out.println(id);
//					skeletonCapability.getSkeletonJointPosition(id, SkeletonJoint.HEAD).getPosition();
				}
			});
			
			if(!userGenerator.isGenerating())
				userGenerator.startGenerating();
			
			new Thread(this).start();
		}
		catch(Exception e) {
			termintate(e);
		}
	}

	@Override
	public void run() {
		while(true) {
			try {
				userGenerator.waitAndUpdateData();
			} catch (StatusException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}
}
