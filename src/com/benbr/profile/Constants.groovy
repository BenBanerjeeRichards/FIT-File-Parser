package com.benbr.profile

import com.benbr.BiMap
import com.benbr.converter.Unit

class Constants {


    // Hash map used instead of an array as it provides more flexibility when assigning
    // indexes to the strings - there are some cases when the ID's do not increment by 1 -
    // using a map is more clear

    // Generated from fit_example.h in the C FIT SDK
    public static final HashMap messageIdToName= [
            0 : "file_id",
            1 : "capabilities",
            2 : "device_settings",
            3 : "user_profile",
            4 : "hrm_profile",
            5 : "sdm_profile",
            6 : "bike_profile",
            7 : "zones_target",
            8 : "hr_zone",
            9 : "power_zone",
            10 : "met_zone",
            12 : "sport",
            15 : "goal",
            18 : "session",
            19 : "lap",
            20 : "record",
            21 : "event",
            23 : "device_info",
            26 : "workout",
            27 : "workout_step",
            28 : "schedule",
            30 : "weight_scale",
            31 : "course",
            32 : "course_point",
            33 : "totals",
            34 : "activity",
            35 : "software",
            37 : "file_capabilities",
            38 : "mesg_capabilities",
            39 : "field_capabilities",
            49 : "file_creator",
            51 : "blood_pressure",
            53 : "speed_zone",
            55 : "monitoring",
            72 : "training_file",
            78 : "hrv",
            80 : "ant_rx",
            81 : "ant_tx",
            82 : "ant_channel_id",
            101 : "length",
            103 : "monitoring_info",
            105 : "pad",
            106 : "slave_device",
            127 : "connectivity",
            128 : "weather_conditions",
            129 : "weather_alert",
            131 : "cadence_zone",
            132 : "hr",
            142 : "segment_lap",
            145 : "memo_glob",
            148 : "segment_id",
            149 : "segment_leaderboard_entry",
            150 : "segment_point",
            151 : "segment_file",
            160 : "gps_metadata",
            161 : "camera_event",
            162 : "timestamp_correlation",
            164 : "gyroscope_data",
            165 : "accelerometer_data",
            167 : "three_d_sensor_calibration",
            169 : "video_frame",
            174 : "obdii_data",
            177 : "nmea_sentence",
            178 : "aviation_attitude",
            184 : "video",
            185 : "video_title",
            186 : "video_description",
            187 : "video_clip",
            200 : "exd_screen_configuration",
            201 : "exd_data_field_configuration",
            202 : "exd_data_concept_configuration",
            206 : "field_description",
            207 : "developer_data_id"
    ]


    // TODO sort this mapping out.
    // s => signed, u => unsigned, z => ?
    public static final HashMap<Integer, String> baseTypes = [
            (0)   : "enum",
            (1)   : "sint8",
            (2)   : "uint8",
            (131) : "sint16",
            (132) : "uint16",
            (133) : "sint32",
            (134) : "uint32",
            (7)   : "string",
            (136) : "float32",
            (137) : "float64",
            (10)  : "uint8z",
            (139) : "uint16z",
            (140) : "uint32z",
            (13)  : "byte",
    ]

    public static BiMap unitToSymbol;



}
