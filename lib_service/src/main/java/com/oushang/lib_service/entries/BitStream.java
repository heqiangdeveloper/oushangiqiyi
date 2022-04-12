package com.oushang.lib_service.entries;

/**
 * @Author: zeelang
 * @Description: 码流
 * @Time: 2021/8/24 18:33
 * @Since: 1.0
 */
public class BitStream {
    private int mDefinition;
    private int mAudioType;
    private int mCodecType;
    private int mDynamicRangeType;
    private int mBenefitType;
    private int mCtrlType;
    private int mVideoPreviewTime;
    private int mDolbyPreviewTime;
    private int mChannelType;

    public BitStream() {

    }

    public int getDefinition() {
        return mDefinition;
    }

    public void setDefinition(int definition) {
        this.mDefinition = definition;
    }

    public int getAudioType() {
        return mAudioType;
    }

    public void setAudioType(int audioType) {
        this.mAudioType = audioType;
    }

    public int getCodecType() {
        return mCodecType;
    }

    public void setCodecType(int codecType) {
        this.mCodecType = codecType;
    }

    public int getDynamicRangeType() {
        return mDynamicRangeType;
    }

    public void setDynamicRangeType(int dynamicRangeType) {
        this.mDynamicRangeType = dynamicRangeType;
    }

    public int getBenefitType() {
        return mBenefitType;
    }

    public void setBenefitType(int benefitType) {
        this.mBenefitType = benefitType;
    }

    public int getCtrlType() {
        return mCtrlType;
    }

    public void setCtrlType(int ctrlType) {
        this.mCtrlType = ctrlType;
    }

    public int getVideoPreviewTime() {
        return mVideoPreviewTime;
    }

    public void setVideoPreviewTime(int videoPreviewTime) {
        this.mVideoPreviewTime = videoPreviewTime;
    }

    public int getDolbyPreviewTime() {
        return mDolbyPreviewTime;
    }

    public void setDolbyPreviewTime(int dolbyPreviewTime) {
        this.mDolbyPreviewTime = dolbyPreviewTime;
    }

    public int getChannelType() {
        return mChannelType;
    }

    public void setChannelType(int channelType) {
        this.mChannelType = channelType;
    }

    @Override
    public String toString() {
        return "BitStream{" +
                "mDefinition=" + mDefinition +
                ", mAudioType=" + mAudioType +
                ", mCodecType=" + mCodecType +
                ", mDynamicRangeType=" + mDynamicRangeType +
                ", mBenefitType=" + mBenefitType +
                ", mCtrlType=" + mCtrlType +
                ", mVideoPreviewTime=" + mVideoPreviewTime +
                ", mDolbyPreviewTime=" + mDolbyPreviewTime +
                ", mChannelType=" + mChannelType +
                '}';
    }

    public static final class CtrlType {
        public static final int CTRL_NONE = -1;
        public static final int CTRL_VIP = 0;
        public static final int CTRL_LOGIN = 1;

        public CtrlType() {
        }
    }

    public static final class BenefitType {
        public static final int CAN_PLAY = 0;
        public static final int CAN_NOT_PLAY = 1;
        public static final int PREVIEW = 2;

        public BenefitType() {
        }
    }

    public static final class DynamicRangeType {
        public static final int SDR = 0;
        public static final int DOLBY_VISION_MASTER = 1;
        public static final int HDR10 = 2;
        public static final int DOLBY_VISION = 3;

        public DynamicRangeType() {
        }
    }

    public static final class AudioChannelType {
        public static final int AUDIOCHANNEL_STEREO = 1;
        public static final int AUDIOCHANNEL_5_1 = 2;
        public static final int AUDIOCHANNEL_7_1 = 3;
        public static final int AUDIOCHANNEL_ATOMS = 4;

        public AudioChannelType() {
        }
    }

    public static final class CodecType {
        public static final int H264 = 0;
        public static final int H211 = 1;

        public CodecType() {
        }
    }

    public static final class AudioType {
        public static final int NORMAL = 0;
        public static final int DOLBY = 1;

        public AudioType() {
        }
    }

    public static final class Definition {
        public static final int DEFINITION_UNKNOWN = 0;
        public static final int DEFINITION_STANDARD = 1;
        public static final int DEFINITION_HIGH = 2;
        public static final int DEFINITION_720P = 4;
        public static final int DEFINITION_1080P = 5;
        public static final int DEFINITION_4K = 10;

        public Definition() {
        }
    }
}
