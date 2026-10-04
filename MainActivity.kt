package com.pepsiballtournament

import android.app.Activity
import android.os.Bundle
import android.graphics.Color
import android.view.Gravity
import android.view.View
import android.widget.*
import kotlin.math.round

data class Ball(val label:String, val legal:Boolean=true, val runs:Int=0, val wicket:Boolean=false)

class MainActivity : Activity() {
    private val blue = Color.rgb(13,71,161)
    private var total = 0
    private var wickets = 0
    private var legalBalls = 0
    private var history = mutableListOf<Triple<Int,Int,Int>>()
    private lateinit var score: TextView
    private lateinit var overs: TextView
    private lateinit var status: TextView

    override fun onCreate(b: Bundle?) { super.onCreate(b); home() }

    private fun base(): LinearLayout { return LinearLayout(this).apply { orientation=LinearLayout.VERTICAL; setPadding(24,24,24,24); setBackgroundColor(Color.WHITE) } }
    private fun title(t:String): TextView = TextView(this).apply { text=t; textSize=24f; setTextColor(blue); gravity=Gravity.CENTER; setPadding(8,18,8,18) }
    private fun btn(t:String, action:()->Unit): Button = Button(this).apply { text=t; setOnClickListener { action() }; isAllCaps=false }
    private fun add(parent:LinearLayout,v:View){ parent.addView(v, LinearLayout.LayoutParams(-1,-2).apply{setMargins(0,8,0,8)}) }

    private fun home(){
        val l=base(); add(l,title("🏏 Pepsi Ball Tournament"));
        val info=TextView(this).apply{text="📍 Mera Cricket Ground\n16 Teams • Knockout\n4 Overs • 5 Wickets"; textSize=18f; gravity=Gravity.CENTER; setPadding(0,10,0,20)}; add(l,info)
        add(l,btn("🏆 Tournament Bracket"){ bracket() }); add(l,btn("⚾ Start Live Scoring"){ scoring() }); add(l,btn("📋 Rules"){ rules() });
        setContentView(l)
    }
    private fun bracket(){
        val l=base(); add(l,title("🏆 Knockout Bracket"));
        val names=(1..16).map{"Team $it"}
        for(i in 0 until 16 step 2) add(l,TextView(this).apply{text="R16: ${names[i]}  vs  ${names[i+1]}"; textSize=16f})
        add(l,btn("Start Scoring"){scoring()}); add(l,btn("Back"){home()}); setContentView(l)
    }
    private fun rules(){
        val l=base(); add(l,title("📋 Tournament Rules")); add(l,TextView(this).apply{text="• 16 teams, knockout format\n• 4 overs per innings\n• Maximum 5 wickets\n• Ball-by-ball scoring\n• Wide/No-ball/Bye/Leg-bye supported\n• Undo last ball\n• Scorecard: runs, balls, 4s, 6s, SR\n• Bowler: overs, runs, wickets\n• Winner advances to next round"; textSize=17f}); add(l,btn("Back"){home()}); setContentView(l)
    }
    private fun scoring(){
        total=0; wickets=0; legalBalls=0; history.clear()
        val l=base(); add(l,title("⚾ Live Scoring"));
        score=TextView(this).apply{textSize=32f; gravity=Gravity.CENTER; setTextColor(blue)}; add(l,score)
        overs=TextView(this).apply{textSize=19f; gravity=Gravity.CENTER}; add(l,overs)
        status=TextView(this).apply{textSize=16f; gravity=Gravity.CENTER}; add(l,status)
        val grid=GridLayout(this).apply{columnCount=3}
        val actions=listOf("0","1","2","3","4","6","WICKET","WIDE","NO BALL","BYE","LEG-BYE","UNDO")
        actions.forEach{t-> val b=Button(this).apply{text=t; setOnClickListener{ if(t=="UNDO") undo() else ball(t)}}; grid.addView(b,GridLayout.LayoutParams().apply{width=0; height=wrapContent; columnSpec=GridLayout.spec(GridLayout.UNDEFINED,1f); setMargins(4,4,4,4)}) }
        add(l,grid); add(l,btn("🏠 Home"){home()}); update(); setContentView(l)
    }
    private fun ball(type:String){
        if(wickets>=5 || legalBalls>=24){ status.text="Innings complete — 4 overs / 5 wickets reached"; return }
        history.add(Triple(total,wickets,legalBalls))
        when(type){
            "WICKET"->{wickets++; legalBalls++}
            "WIDE"->{total++}
            "NO BALL"->{total++; /* illegal ball */}
            "BYE","LEG-BYE"->{legalBalls++}
            else->{total += type.toInt(); legalBalls++}
        }
        update()
    }
    private fun undo(){ if(history.isNotEmpty()){val x=history.removeAt(history.lastIndex); total=x.first; wickets=x.second; legalBalls=x.third; update()} }
    private fun update(){
        score.text="$total / $wickets"; overs.text="Overs: ${legalBalls/6}.${legalBalls%6} / 4.0"; status.text=if(legalBalls>=24||wickets>=5)"Innings Complete" else "Live — tap a scoring button"
    }
}
