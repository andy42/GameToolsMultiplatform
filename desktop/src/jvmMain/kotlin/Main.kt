import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.jaehl.gameTool.apiClientRetrofit.di.ApiClientRetrofitModule
import com.jaehl.gameTool.common.App
import com.jaehl.gameTool.common.di.DataModule
import com.jaehl.gameTool.common.di.ScreenModule
import org.kodein.di.*


fun main() = application {
    Window(onCloseRequest = ::exitApplication) {
        val di = DI {
            bind<String>(tag = "baseUrl") { provider { "http://0.0.0.0:8080" }  }
            import(ScreenModule.create())
            import(DataModule.create())
            import(ApiClientRetrofitModule.create())
        }
        App(di)
    }
}
