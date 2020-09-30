BOX_IMAGE = "geerlingguy/centos7"
NODE_COUNT = 4

Vagrant.configure("2") do |config|
    (1..NODE_COUNT).each do |i|
        config.vm.define "bank#{i}" do |subconfig|
            subconfig.vm.box = BOX_IMAGE
            subconfig.vm.hostname = "node#{i}"
            subconfig.vm.network :private_network, ip: "192.168.10.#{i + 9}"

                subconfig.trigger.after :up do |trigger|
                    trigger.info = "starting bank"
                    trigger.run_remote = {inline: <<-SHELL
			export JAVA_HOME=/usr/lib/jvm/jre-11-openjdk-11.0.8.10-0.el7_8.x86_64/
			export PATH=$JAVA_HOME/bin:$PATH
			java -jar target/bankingapp-jar-with-dependencies.jar topology.txt #{i - 1} &
		    	SHELL
		    }
                end
        end
    end
    config.vm.synced_folder ".", "/home/vagrant"
    config.vm.provision "shell", path: "provision.sh"
end
