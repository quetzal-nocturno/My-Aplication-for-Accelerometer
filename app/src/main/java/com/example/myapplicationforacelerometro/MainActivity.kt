package com.example.myapplicationforacelerometro

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity(), SensorEventListener {

    var x : Double = 0.0
    var y : Double = 0.0
    var z : Double = 0.0
    var lonEu : Double = 0.0
    var leMax : Double = 0.0

    var contador : Int = 0

    lateinit var tvAX : TextView
    lateinit var tvAY : TextView
    lateinit var tvAZ : TextView
    lateinit var tvLE : TextView
    lateinit var tvMax : TextView
    lateinit var tvGravedad : TextView
    lateinit var tvContador : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val administrador : SensorManager = getSystemService(Context.SENSOR_SERVICE)
            as SensorManager
        val acelerometro : Sensor? = administrador.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        administrador.registerListener(this, acelerometro, SensorManager.SENSOR_DELAY_NORMAL)

        tvAX = findViewById(R.id.tvAX) as TextView
        tvAY = findViewById(R.id.tvAY) as TextView
        tvAZ = findViewById(R.id.tvAZ) as TextView
        tvLE = findViewById(R.id.tvLE) as TextView
        tvMax = findViewById(R.id.tvMax) as TextView
        tvContador = findViewById(R.id.tvContador) as TextView
        tvGravedad = findViewById(R.id.tvGravedad) as TextView

        tvGravedad.setText("" + SensorManager.STANDARD_GRAVITY)

        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "WarningChannel"
            val descriptionText = "Channel for movement warnings"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("WARNING_CHANNEL", name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    override fun onSensorChanged(event: SensorEvent) {
        val mySensor = event.sensor

        if (mySensor.type == Sensor.TYPE_ACCELEROMETER) {
            x = event.values[0].toDouble()
            y = event.values[1].toDouble()
            z = event.values[2].toDouble()

            lonEu = Math.sqrt(x * x + y * y + z * z)
            if (lonEu > leMax) leMax = lonEu

            tvAX.text = "x: $x"
            tvAY.text = "y: $y"
            tvAZ.text = "z: $z"

            tvLE.text = "LE: $lonEu"
            tvMax.text = "LEMax: $leMax"
            tvContador.text = "Cont: $contador"
            contador++

            if (lonEu > 15) {
                Toast.makeText(this, "Ya!! wey", Toast.LENGTH_SHORT).show()
            } else if (lonEu > 20) {
                val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE))
                } else {
                    vibrator.vibrate(500)
                }
                Toast.makeText(this, "Pararale weyyy..", Toast.LENGTH_SHORT).show()
            } else if (lonEu > 25) {
                sendPushNotification()
            }
        }
    }

    private fun sendPushNotification() {
        val builder = NotificationCompat.Builder(this, "WARNING_CHANNEL")
            .setSmallIcon(R.drawable.notification_icon)
            .setContentTitle("Alerta")
            .setContentText("Pinche pendejo")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        with(NotificationManagerCompat.from(this)) {
            notify(1, builder.build())
        }
    }


    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }
}