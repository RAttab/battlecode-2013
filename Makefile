#------------------------------------------------------------------------------#
# Makefile
# Rémi Attab (remi.attab@gmail.com), 11 Jan 2013
# FreeBSD-style copyright and disclaimer apply
#
# Makefile to setup and run our project.
#------------------------------------------------------------------------------#

INSTALL_DIR ?= ~/Battlecode2013

BOTS := team216 \
	testplayer \
	rusher

install: $(foreach bot,$(BOTS),$(INSTALL_DIR)/teams/$(bot))

$(INSTALL_DIR)/teams/%:
	 ln -s $(CURDIR)/$* $@

template:
	cat team216/Weights.java | \
	sed -r 's/([_A-Z]+)\s*=\s*(-?[0-9]*\.?[0-9]*);/\1 = %{\1};/' > Weights.tpl
	cat team216/Weights.java | \
	egrep '=\s*(-?[0-9]*\.?[0-9]*);' | \
	sed -r 's/.*\s([_A-Z]+)\s*=\s*(-?[0-9]*\.?[0-9]*);.*/\1=\2;/' > genome.txt
