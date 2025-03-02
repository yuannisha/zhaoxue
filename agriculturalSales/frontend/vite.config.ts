import { defineConfig } from 'vite'
import { fileURLToPath, URL } from 'node:url'
import vue from '@vitejs/plugin-vue'
import{resolve} from 'path'
import legacy from '@vitejs/plugin-legacy';

// https://vite.dev/config/
export default defineConfig({
  plugins: [vue(),
    legacy({
			targets:["defaults","not IE 11"],
		})
  ],
  server: {
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        secure:true,
        ws:true,
        rewrite:(path) => path.replace(/^\/api/, '')
      }
    }
  },
  base:'./',
  // 路径别名
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url)),
      // 组件路径别名,语法：别名：resolve(__dirname,'组件原路径')
      com:resolve(__dirname,'src/component'),
      //静态资源路径别名
      './image':'src/assets'
    }
  },
  // 生产环境移除console.log的配置
  build:{
    // 默认是esbuild,但这里需要改成terser，并且想使用terser的话，需提前安装，命令为npm add -D terser
    minify:"terser",
    terserOptions: {
        compress: {
          //生产环境时移除console
          drop_console: true,
          drop_debugger: true,
        },
      },
  }
})
