# 自动部署说明

代码推送到 `main` 后，GitHub Actions 会构建前端和后端，并通过 SSH
把发布包部署到服务器。

## 1. 初始化服务器

在本机 PowerShell 上传部署脚本：

```powershell
scp -r "D:\kiro\deploy" root@59.110.175.98:/root/xiao-club-deploy
```

登录服务器：

```powershell
ssh root@59.110.175.98
```

安装运行环境并执行初始化：

```bash
apt update
apt install -y openjdk-17-jre-headless nginx
cd /root/xiao-club-deploy/deploy
bash setup-server.sh
```

## 2. 配置数据库

查看 MySQL 实际监听端口：

```bash
ss -lntp | grep mysqld
```

编辑后端环境变量：

```bash
nano /etc/xiao-club/xiao-club.env
```

示例：

```properties
DB_URL=jdbc:mysql://127.0.0.1:3306/xiao_club?useUnicode=true&characterEncoding=utf-8&serverTimezone=Asia/Shanghai
DB_USERNAME=xiao_club
DB_PASSWORD=替换为真实密码
JAVA_OPTS=-Xms256m -Xmx512m
```

如果 MySQL 实际监听 `3344`，把 URL 中的 `3306` 改成 `3344`。

## 3. 授权 GitHub Actions 登录服务器

在服务器执行：

```bash
mkdir -p ~/.ssh
chmod 700 ~/.ssh
echo 'ssh-ed25519 AAAAC3NzaC1lZDI1NTE5AAAAIE4RBqd2Fi0PdlTy0GTfrHXQBzENQ4/DnjY5S1zTyrBv github-actions-xiao-club' >> ~/.ssh/authorized_keys
chmod 600 ~/.ssh/authorized_keys
```

## 4. 配置 GitHub Secrets

打开仓库：

`Settings -> Secrets and variables -> Actions -> New repository secret`

添加：

| 名称 | 值 |
| --- | --- |
| `SERVER_HOST` | `59.110.175.98` |
| `SERVER_PORT` | `22` |
| `SERVER_USER` | `root` |
| `SERVER_SSH_KEY` | 本机 `C:\Users\32284\.ssh\xiao-club-github-actions` 文件的完整内容 |

`SERVER_SSH_KEY` 必须填写私钥文件内容，不是 `.pub` 公钥内容。

## 5. 触发部署

完成上述配置后，将代码提交并推送：

```powershell
git add .
git commit -m "ci: add automatic deployment"
git push origin main
```

在 GitHub 仓库的 `Actions` 页面可以查看部署进度。

部署完成后访问：

`http://59.110.175.98`

## 常用排查命令

```bash
systemctl status xiao-club --no-pager
journalctl -u xiao-club -n 100 --no-pager
nginx -t
curl -I http://127.0.0.1
```
